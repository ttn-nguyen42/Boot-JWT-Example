package com.example.authentication_example.utils;

import java.time.Clock;
import java.util.Date;

public class Utils {
    public static String getTimestamp() {
        return Date.from(Clock.systemUTC().instant()).toString();
    }
}
