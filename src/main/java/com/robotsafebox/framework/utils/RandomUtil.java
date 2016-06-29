package com.robotsafebox.framework.utils;

import java.util.Random;

public class RandomUtil {

    private static char[] codeSequence = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static String getRandom(int num) {

        String password = "";

        Random random = new Random();
        for (int i = 0; i < num; i++) {
            password += String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
        }

        return password;
    }

}