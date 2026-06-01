package com.jerry.wallet.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Configuration
@EntityScan("com.jerry.wallet.model")
public class CustomConfiguration {

//    @Bean
//    public Gson gson() {
//        return new GsonBuilder()
//                .setDateFormat(TimeFormatConstants.DEFAULT_DATE_TIME_FORMAT)
//                .registerTypeAdapter(OffsetTime.class, new OffsetTimeConverter())
//                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter())
//                .registerTypeAdapter(LocalTime.class, new LocalTimeConverter())
//                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
//                    try {
//                        return DateUtils.parseDate(json.getAsJsonPrimitive().getAsString(), TimeFormatConstants.DEFAULT_DATE_TIME_FORMAT,
//                                "yyyy-MM-dd'T'HH:mm:ss.Z",
//                                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
//                                "yyyy-MM-dd'T'HH:mm:ss.SSS",
//                                "yyyy-MM-dd HH:mm:ss.SSS",
//                                "MM/dd/yyyy HH:mm:ss");
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    return null;
//                })
//                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) -> LocalDate.parse(json.getAsJsonPrimitive().getAsString()))
//                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (localDate, type, jsonSerializationContext) -> new JsonPrimitive(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)))
//                .create();
//    }
}
