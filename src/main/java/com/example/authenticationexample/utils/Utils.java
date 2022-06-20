package com.example.authenticationexample.utils;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;

public class Utils {
    public static String getTimestamp() {
        return Date.from(Clock.systemUTC().instant()).toString();
    }
}
