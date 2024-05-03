package com.coruptical.townshipcore.economy;

public enum EconomyPermissions {
    BALANCE(EconomyHandler.ENUM_PREFIX + "balance");

    private static String name;

    EconomyPermissions(String name){

    };

    public static String getName() {
        return name;
    }
}
