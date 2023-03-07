package com.appunite.loudius.network.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): LocalDateTime {
        try {
            val dateString = json?.asJsonPrimitive?.asString
            val offsetDateTime = OffsetDateTime.parse(dateString, ISO_OFFSET_DATE_TIME)
            return offsetDateTime.toLocalDateTime()
        } catch (e: Exception) {
            throw JsonParseException(e)
        }
    }
}
