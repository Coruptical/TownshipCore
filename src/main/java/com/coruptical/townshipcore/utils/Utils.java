package com.coruptical.townshipcore.utils;

import java.util.Locale;

public class Utils {
    public static double round(double number){
        String roundedString = String.format(Locale.US, "%.2f", number);
        return Double.parseDouble(roundedString);
    }
}
