package com.coruptical.townshipcore.economy;

public enum EconomyPermissions {
    BALANCE(EconomyHandler.ENUM_PREFIX + "balance");

    private final String name;

    public String getName() {
        return name;
    }

    EconomyPermissions(String name){
        this.name = name;
    }
}
