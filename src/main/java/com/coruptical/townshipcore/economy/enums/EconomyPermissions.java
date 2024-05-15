package com.coruptical.townshipcore.economy.enums;

import com.coruptical.townshipcore.economy.EconomyHandler;

public enum EconomyPermissions {
    BALANCE(EconomyHandler.ENUM_PREFIX + "balance"),
    ECONOMY(EconomyHandler.ENUM_PREFIX + "economy"),
    BALANCETOP(EconomyHandler.ENUM_PREFIX + "balancetop");

    private final String name;

    public String getName() {
        return name;
    }

    EconomyPermissions(String name){
        this.name = name;
    }
}
