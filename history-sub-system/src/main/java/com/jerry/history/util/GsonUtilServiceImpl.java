package com.jerry.history.util;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.jerry.history.service.GsonUtilService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

@Service
public class GsonUtilServiceImpl implements GsonUtilService {

    @Override
    public Gson deserializedGson() {
        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapter(Timestamp.class, (JsonSerializer<Timestamp>) (date, type, jsonSerializationContext) ->
                new JsonPrimitive(date.getTime()));
        b.registerTypeAdapter(Instant.class, new TypeAdapter<Instant>() {
            @Override
            public void write(JsonWriter jsonWriter, Instant instant) throws IOException {
                if (instant == null) {
                    jsonWriter.nullValue();
                } else {
                    jsonWriter.value(instant.toString());
                }
            }

            @Override
            public Instant read(JsonReader jsonReader) throws IOException {
                JsonToken token = jsonReader.peek();
                if (token == JsonToken.NULL) {
                    jsonReader.nextNull();
                    return null;
                }
                if (token == JsonToken.NUMBER) {
                    return Instant.ofEpochMilli(jsonReader.nextLong());
                }
                String value = jsonReader.nextString().trim();
                if (value.matches("-?\\d+")) {
                    return Instant.ofEpochMilli(Long.parseLong(value));
                }
                try {
                    return Instant.parse(value);
                } catch (DateTimeParseException ignored) {
                }
                try {
                    return OffsetDateTime.parse(value).toInstant();
                } catch (DateTimeParseException ignored) {
                }
                return LocalDateTime.parse(value.replace(' ', 'T')).toInstant(ZoneOffset.UTC);
            }
        });
        b.registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
            @Override
            public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
                if (localDateTime == null) {
                    jsonWriter.nullValue();
                } else {
                    jsonWriter.value(DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH:mm:ss.SSS").format(localDateTime));
                }
            }

            @Override
            public LocalDateTime read(JsonReader jsonReader) throws IOException {
                if (Objects.requireNonNull(jsonReader.peek()) == JsonToken.NULL) {
                    jsonReader.nextNull();
                    return null;
                }
                String date = jsonReader.nextString();
                try {
                    return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
                } catch (DateTimeParseException ignored) {

                }
                try {
                    return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS"));
                } catch (DateTimeParseException ignored) {

                }
                try {
                    return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S"));
                } catch (DateTimeParseException ignored) {

                }
                try {
                    return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                } catch (DateTimeParseException ignored) {

                }
                return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            }
        });
        b.setDateFormat("yyyy-MM-dd");
        return b.create();
    }
}
