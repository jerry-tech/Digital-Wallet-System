package com.jerry.wallet.enumuration;

import java.util.Arrays;
import java.util.List;


public class TimeFormatConstants {

    public static final String DD_MM_YYYY_SLASHED = "dd/MM/yyyy";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    public static final String APP_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final List<String> SUPPORTED_FORMATS = Arrays.asList(DEFAULT_DATE_FORMAT, DEFAULT_DATE_TIME_FORMAT,
            APP_DATE_TIME_FORMAT, DD_MM_YYYY_SLASHED, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd HH:mm:ss.SSS");
}
