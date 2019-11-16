package com.github.adrianhall.weather.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.io.IOException
import java.util.*

/**
 * Deserializer for the UNIX Timestamp that Dark Sky uses
 */
class UnixTimestampDeserializer : JsonDeserializer<Date>() {
    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Date {
        val unixTimestamp = parser.text.trim { it <= ' ' }.toLong()
        return Date(unixTimestamp * 1000L)
    }
}