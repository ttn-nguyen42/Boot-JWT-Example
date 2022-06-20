package com.example.authenticationexample.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class RandomHolder {
    static final Random r = new SecureRandom();

    public static String randomKey(int length) {
        return String.format("%" + length + "s", new BigInteger(length * 5, r).toString(32)).replace("\u0020", "0");
    }
}