package com.example.sobesai.core

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun extractDisplayNameFromJwt(token: String): String? {
    return runCatching {
        val payload = token.split(".").getOrNull(1) ?: return null
        val normalized = payload
            .replace('-', '+')
            .replace('_', '/')
            .let { value ->
                value + "=".repeat((4 - value.length % 4) % 4)
            }

        val payloadJson = Base64.decode(normalized).decodeToString()
        val userMetadata = Json.parseToJsonElement(payloadJson)
            .jsonObject["user_metadata"]
            ?.jsonObject

        userMetadata?.get("user_name")?.jsonPrimitive?.content
            ?: userMetadata?.get("name")?.jsonPrimitive?.content
            ?: userMetadata?.get("full_name")?.jsonPrimitive?.content
            ?: userMetadata?.get("preferred_username")?.jsonPrimitive?.content
    }.getOrNull()
}
