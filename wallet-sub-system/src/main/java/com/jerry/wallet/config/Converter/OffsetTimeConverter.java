package com.jerry.wallet.config.Converter;

import com.google.gson.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.lang.reflect.Type;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

public final class OffsetTimeConverter implements Converter<String, OffsetTime>, JsonSerializer<OffsetTime>, JsonDeserializer<OffsetTime> {

    /** Formatter. */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_TIME;

    @Override
    public OffsetTime convert(String source) {
        if (StringUtils.isBlank(source) || source.isEmpty()) {
            return null;
        }
        return OffsetTime.parse(source, FORMATTER);
    }

    @Override
    public JsonElement serialize(OffsetTime src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(FORMATTER.format(src));
    }

    @Override
    public OffsetTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        return FORMATTER.parse(json.getAsString(), OffsetTime::from);
    }
}
