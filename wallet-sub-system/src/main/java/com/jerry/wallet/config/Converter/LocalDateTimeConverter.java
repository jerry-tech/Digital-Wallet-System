package com.jerry.wallet.config.Converter;

import com.google.gson.*;
import org.springframework.core.convert.converter.Converter;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class LocalDateTimeConverter implements Converter<String, LocalDateTime>, JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    private final DateTimeFormatter formatter;

    public LocalDateTimeConverter(String dateFormat) {
        this.formatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    public LocalDateTimeConverter() {
        this.formatter = DateTimeFormatter.ISO_DATE_TIME;
    }

    @Override
    public LocalDateTime convert(String source) {
        if (source.isEmpty()) {
            return null;
        }

        return LocalDateTime.parse(source, formatter);
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), formatter);
    }

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(formatter.format(src));
    }
}
