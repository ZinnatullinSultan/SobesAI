package com.example.sobesai.core.liveapi

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val GEMINI_LIVE_WS_URL = "wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1beta.GenerativeService.BidiGenerateContent"

private const val LOG_TAG = "GeminiLiveWebSocket"

/**
 * WebSocket client for Gemini Live API
 */
class GeminiLiveWebSocketClient(
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    private var client: HttpClient? = null
    private var session: DefaultClientWebSocketSession? = null
    private var receiveJob: Job? = null

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _userTranscript = MutableStateFlow("")
    val userTranscript: StateFlow<String> = _userTranscript.asStateFlow()

    private val _aiResponse = MutableStateFlow("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    private val audioOutputChannel = Channel<ByteArray>(Channel.UNLIMITED)

    sealed class ConnectionState {
        object Disconnected : ConnectionState()
        object Connecting : ConnectionState()
        object Connected : ConnectionState()
        object Ready : ConnectionState()  // After setupComplete received
        data class Error(val message: String) : ConnectionState()
    }

    private val _setupComplete = kotlinx.coroutines.channels.Channel<Unit>(1)
    
    suspend fun waitForSetup(): Boolean {
        return try {
            withTimeout(10000) { // 10 second timeout
                _setupComplete.receive()
            }
            _connectionState.value = ConnectionState.Ready
            true
        } catch (e: Exception) {
            println("[$LOG_TAG] waitForSetup error: ${e.message}")
            false
        }
    }

    suspend fun connect(config: GeminiLiveConfig): Boolean {
        return try {
            println("[$LOG_TAG] Connecting to Gemini Live API...")
            _connectionState.value = ConnectionState.Connecting

            client = HttpClient {
                install(WebSockets)
            }

            val url = "$GEMINI_LIVE_WS_URL?key=${config.apiKey}"
            println("[$LOG_TAG] URL: $url")
            session = client!!.webSocketSession(url)

            // Start receiving messages BEFORE sending setup
            startReceiving()

            // Send setup message
            val setupMessage = createSetupMessage(config)
            val setupJson = json.encodeToString(setupMessage)
            println("[$LOG_TAG] Sending setup: $setupJson")
            session!!.send(Frame.Text(setupJson))

            _connectionState.value = ConnectionState.Connected
            println("[$LOG_TAG] WebSocket connected, waiting for setupComplete...")

            true
        } catch (e: Exception) {
            println("[$LOG_TAG] Connection failed: ${e.message}")
            e.printStackTrace()
            _connectionState.value = ConnectionState.Error(e.message ?: "Connection failed")
            false
        }
    }

    private fun createSetupMessage(config: GeminiLiveConfig): LiveApiSetup {
        return LiveApiSetup(
            config = SetupConfig(
                model = "models/${config.modelName}",
                responseModalities = listOf("AUDIO"),
                systemInstruction = config.systemInstruction?.let {
                    Content(
                        parts = listOf(Part(text = it))
                    )
                }
            )
        )
    }

    private fun startReceiving() {
        val currentSession = session ?: return
        receiveJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                currentSession.incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        handleMessage(frame.readText())
                    } else if (frame is Frame.Binary) {
                        // Handle binary audio data
                        val audioData = frame.data
                        audioOutputChannel.trySend(audioData)
                    }
                }
            } catch (e: Exception) {
                if (currentSession.isActive) {
                    _connectionState.value = ConnectionState.Error(e.message ?: "Receive error")
                }
            }
        }
    }

    private fun handleMessage(messageText: String) {
        println("[$LOG_TAG] Received: $messageText")
        try {
            val response = json.decodeFromString<LiveApiResponse>(messageText)

            when {
                response.serverContent != null -> {
                    println("[$LOG_TAG] Got serverContent")
                    // Handle AI response - audio comes in inlineData
                    response.serverContent.modelTurn?.parts?.forEach { part ->
                        part.inlineData?.let { inlineData ->
                            if (inlineData.mimeType.startsWith("audio/")) {
                                println("[$LOG_TAG] Received audio: ${inlineData.mimeType}, ${inlineData.data.length} chars")
                                // Decode base64 audio and send to playback channel
                                // For now we just log it
                            }
                        }
                        part.text?.let { 
                            println("[$LOG_TAG] AI part text: $it")
                            _aiResponse.value = it 
                        }
                    }
                    // Handle input transcription (user speech)
                    response.serverContent.inputTranscription?.text?.let { text ->
                        println("[$LOG_TAG] User transcription: $text")
                        _userTranscript.value = text
                    }
                    // Handle output transcription (AI speech)
                    response.serverContent.outputTranscription?.text?.let { text ->
                        println("[$LOG_TAG] AI transcription: $text")
                    }
                    if (response.serverContent.turnComplete == true) {
                        println("[$LOG_TAG] Turn complete")
                    }
                }

                response.setupComplete != null -> {
                    println("[$LOG_TAG] Setup complete! Ready for audio.")
                    _setupComplete.trySend(Unit)
                }

                response.error != null -> {
                    println("[$LOG_TAG] Server error: ${response.error}")
                    _connectionState.value = ConnectionState.Error(response.error.message ?: "Unknown error")
                }

                else -> {
                    println("[$LOG_TAG] Unknown response structure")
                }
            }
        } catch (e: Exception) {
            println("[$LOG_TAG] Error parsing message: ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun sendAudioChunk(audioData: ByteArray) {
        if (_connectionState.value !is ConnectionState.Ready) {
            println("[$LOG_TAG] Cannot send audio - not ready, state: ${_connectionState.value}")
            return
        }

        try {
            // Convert audio to Base64
            val base64Data = Base64Encoder.encode(audioData)
            
            val audioMessage = LiveApiAudioInput(
                realtimeInput = RealtimeInput(
                    audio = AudioBlob(
                        mimeType = "audio/pcm;rate=16000",
                        data = base64Data
                    )
                )
            )
            val messageJson = json.encodeToString(audioMessage)
            session?.send(Frame.Text(messageJson))
        } catch (e: Exception) {
            println("[$LOG_TAG] Error sending audio: ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun sendAudioEnd() {
        if (_connectionState.value !is ConnectionState.Connected) return

        try {
            val endMessage = LiveApiAudioEnd(
                clientContent = ClientContent(
                    turns = emptyList(),
                    turnComplete = true
                )
            )
            session?.send(Frame.Text(json.encodeToString(endMessage)))
        } catch (e: Exception) {
            // Handle error
        }
    }

    fun getAudioOutputChannel(): Channel<ByteArray> = audioOutputChannel

    suspend fun disconnect() {
        receiveJob?.cancel()
        receiveJob = null

        session?.close()
        session = null

        client?.close()
        client = null

        _connectionState.value = ConnectionState.Disconnected
        _userTranscript.value = ""
        _aiResponse.value = ""
    }

    fun isConnected(): Boolean = _connectionState.value == ConnectionState.Ready
}

// Data classes for Gemini Live API JSON messages

@Serializable
data class LiveApiSetup(
    val config: SetupConfig
)

@Serializable
data class SetupConfig(
    @SerialName("model")
    val model: String,
    @SerialName("responseModalities")
    val responseModalities: List<String>,
    @SerialName("systemInstruction")
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    @SerialName("parts")
    val parts: List<Part>
)

@Serializable
data class Part(
    @SerialName("text")
    val text: String? = null,
    @SerialName("inlineData")
    val inlineData: InlineData? = null
)

@Serializable
data class InlineData(
    @SerialName("mimeType")
    val mimeType: String,
    @SerialName("data")
    val data: String // Base64 encoded
)

@Serializable
data class LiveApiAudioInput(
    @SerialName("realtimeInput")
    val realtimeInput: RealtimeInput
)

@Serializable
data class RealtimeInput(
    @SerialName("audio")
    val audio: AudioBlob? = null,
    @SerialName("audioStreamEnd")
    val audioStreamEnd: Boolean? = null
)

@Serializable
data class AudioBlob(
    @SerialName("mimeType")
    val mimeType: String,
    @SerialName("data")
    val data: String // Base64 encoded
)

@Serializable
data class LiveApiAudioEnd(
    @SerialName("clientContent")
    val clientContent: ClientContent
)

@Serializable
data class ClientContent(
    @SerialName("turns")
    val turns: List<Content> = emptyList(),
    @SerialName("turnComplete")
    val turnComplete: Boolean
)

@Serializable
data class LiveApiResponse(
    @SerialName("setupComplete")
    val setupComplete: SetupComplete? = null,
    @SerialName("serverContent")
    val serverContent: ServerContent? = null,
    @SerialName("error")
    val error: ApiError? = null
)

@Serializable
data class ApiError(
    @SerialName("code")
    val code: Int? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("status")
    val status: String? = null
)

@Serializable
data class SetupComplete(
    @SerialName("sessionId")
    val sessionId: String? = null
)

@Serializable
data class ServerContent(
    @SerialName("modelTurn")
    val modelTurn: ModelTurn? = null,
    @SerialName("inputTranscription")
    val inputTranscription: Transcription? = null,
    @SerialName("outputTranscription")
    val outputTranscription: Transcription? = null,
    @SerialName("turnComplete")
    val turnComplete: Boolean? = null,
    @SerialName("generationComplete")
    val generationComplete: Boolean? = null,
    @SerialName("interrupted")
    val interrupted: Boolean? = null
)

@Serializable
data class ModelTurn(
    @SerialName("parts")
    val parts: List<Part>? = null
)

@Serializable
data class Transcription(
    @SerialName("text")
    val text: String? = null
)
