package com.example.sobesai.core

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private const val KEY_USER_METADATA = "user_metadata"
private const val KEY_USER_NAME = "user_name"
private const val KEY_NAME = "name"
private const val KEY_FULL_NAME = "full_name"
private const val KEY_PREFERRED_USERNAME = "preferred_username"
private const val JWT_PAYLOAD_INDEX = 1

@OptIn(ExperimentalEncodingApi::class)
fun extractDisplayNameFromJwt(token: String): String? {
    return runCatching {
        val payload = token.split(".").getOrNull(JWT_PAYLOAD_INDEX) ?: return null
        val normalized = payload
            .replace('-', '+')
            .replace('_', '/')
            .let { value ->
                value + "=".repeat((4 - value.length % 4) % 4)
            }

        val payloadJson = Base64.decode(normalized).decodeToString()
        val userMetadata = Json.parseToJsonElement(payloadJson)
            .jsonObject[KEY_USER_METADATA]
            ?.jsonObject

        userMetadata?.get(KEY_USER_NAME)?.jsonPrimitive?.content
            ?: userMetadata?.get(KEY_NAME)?.jsonPrimitive?.content
            ?: userMetadata?.get(KEY_FULL_NAME)?.jsonPrimitive?.content
            ?: userMetadata?.get(KEY_PREFERRED_USERNAME)?.jsonPrimitive?.content
    }.getOrNull()
}
