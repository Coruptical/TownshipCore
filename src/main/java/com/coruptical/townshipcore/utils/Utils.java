package com.coruptical.townshipcore.utils;

import com.coruptical.townshipcore.TownshipCore;
import com.coruptical.townshipcore.models.UUIDResponse;
import com.google.gson.Gson;
import org.bukkit.ChatColor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class Utils {
    public static double round(double number){
        String roundedString = String.format(Locale.US, "%.2f", number);
        return Double.parseDouble(roundedString);
    }

    public static String requestMojangUUID(String name){
        try {
            URL url = new URL(String.format("https://api.mojang.com/users/profiles/minecraft/%s", name));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (con.getResponseCode() != 200){
                return null;
            }

            // Read response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            System.out.println(content);

            // Clean output
            Gson gson = new Gson();
            UUIDResponse uuidResponse = gson.fromJson(content.toString(), UUIDResponse.class);
            return uuidResponse.getId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String color(String msg){
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String pColor(String msg) {
        return ChatColor.translateAlternateColorCodes('&',
                TownshipCore.getInstance().getPrefix() + msg);
    }
}
