package com.jerry.wallet.config;

import com.google.gson.*;
import com.jerry.wallet.config.Converter.LocalDateTimeConverter;
import com.jerry.wallet.config.Converter.LocalTimeConverter;
import com.jerry.wallet.config.Converter.OffsetTimeConverter;
import com.jerry.wallet.enumuration.TimeFormatConstants;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Configuration
@EntityScan("com.jerry.wallet.model")
@EnableScheduling
public class CustomConfiguration {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setDateFormat(TimeFormatConstants.DEFAULT_DATE_TIME_FORMAT)
                .registerTypeAdapter(OffsetTime.class, new OffsetTimeConverter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeConverter())
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
                    try {
                        return DateUtils.parseDate(json.getAsJsonPrimitive().getAsString(), TimeFormatConstants.DEFAULT_DATE_TIME_FORMAT,
                                "yyyy-MM-dd'T'HH:mm:ss.Z",
                                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                                "yyyy-MM-dd HH:mm:ss.SSS",
                                "MM/dd/yyyy HH:mm:ss");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) -> LocalDate.parse(json.getAsJsonPrimitive().getAsString()))
                .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, t, ctx) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (localDate, type, jsonSerializationContext) -> new JsonPrimitive(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .create();
    }
}
