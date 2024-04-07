package com.coruptical.townshipcore;

import org.bukkit.plugin.java.JavaPlugin;

public final class TownshipCore extends JavaPlugin {
    private static TownshipCore instance;

    public static TownshipCore getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        
    }

    @Override
    public void onDisable() {

    }
}
