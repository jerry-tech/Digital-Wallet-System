package com.jerry.wallet.config.Converter;

import com.google.gson.*;
import org.springframework.core.convert.converter.Converter;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class LocalTimeConverter implements Converter<String, LocalTime>, JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

    private final DateTimeFormatter formatter;

    public LocalTimeConverter(String dateFormat) {
        this.formatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    public LocalTimeConverter() {
        this.formatter = DateTimeFormatter.ISO_TIME;
    }

    @Override
    public LocalTime convert(String source) {
        if (source.isEmpty()) {
            return null;
        }

        return LocalTime.parse(source, formatter);
    }

    @Override
    public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return LocalTime.parse(json.getAsJsonPrimitive().getAsString(), formatter);
    }

    @Override
    public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(formatter.format(src));
    }
}
