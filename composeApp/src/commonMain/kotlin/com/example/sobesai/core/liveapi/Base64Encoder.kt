package com.example.sobesai.core.liveapi

/**
 * Cross-platform Base64 encoding for Kotlin Multiplatform
 */
internal object Base64Encoder {
    private val BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    private val PADDING = '='

    fun encode(data: ByteArray): String {
        val result = StringBuilder()
        var i = 0
        
        while (i < data.size) {
            val b0 = data[i].toInt() and 0xFF
            val b1 = if (i + 1 < data.size) data[i + 1].toInt() and 0xFF else 0
            val b2 = if (i + 2 < data.size) data[i + 2].toInt() and 0xFF else 0
            
            result.append(BASE64_CHARS[b0 shr 2])
            result.append(BASE64_CHARS[(b0 and 0x03) shl 4 or (b1 shr 4)])
            
            if (i + 1 < data.size) {
                result.append(BASE64_CHARS[(b1 and 0x0F) shl 2 or (b2 shr 6)])
            } else {
                result.append(PADDING)
            }
            
            if (i + 2 < data.size) {
                result.append(BASE64_CHARS[b2 and 0x3F])
            } else {
                result.append(PADDING)
            }
            
            i += 3
        }
        
        return result.toString()
    }
}
