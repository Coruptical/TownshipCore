package com.coruptical.townshipcore.utils;

import com.coruptical.townshipcore.TownshipCore;
import com.coruptical.townshipcore.economy.EconomyHandler;

import java.util.Locale;

public class Utils {
    public static double round(double number){
        String roundedString = String.format(Locale.US, "%.2f", number);
        return Double.parseDouble(roundedString);
    }

    public static void registerListeners(TownshipCore plugin){
        plugin.getServer().getPluginManager().registerEvents(EconomyHandler.getInstance(), plugin);
    }
}
