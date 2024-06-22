package com.coruptical.townshipcore;

import com.coruptical.townshipcore.economy.EconomyHandler;
import com.coruptical.townshipcore.models.MarketItem;
import com.coruptical.townshipcore.skill.SkillManager;
import com.coruptical.townshipcore.utils.DatabaseHandler;
import com.coruptical.townshipcore.utils.MarketHandler;
import com.coruptical.townshipcore.utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class TownshipCore extends JavaPlugin {
    private static TownshipCore instance;

    private MarketHandler marketHandler;
    private DatabaseHandler databaseHandler;
    private EconomyHandler economyHandler;
    private SkillManager skillManager;

    public DatabaseHandler getDatabaseHandler () {
        return databaseHandler;
    }

    public EconomyHandler getEconomyHandler() {
        return economyHandler;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public static TownshipCore getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Set up database
        databaseHandler = DatabaseHandler.getInstance();
        databaseHandler.init(this);

        // Set up database tables and create economy
        economyHandler = EconomyHandler.getInstance();
        economyHandler.init(this, getDatabaseHandler(), getNoPermissionMessage());

        // Set up market handler and market items
        marketHandler = MarketHandler.getInstance();
        marketHandler.init(this);
        marketHandler.createCustomConfig();

        // Set up skill manager
        skillManager = SkillManager.getInstance();
        skillManager.init(instance, Utils.color(getPrefix()));
    }

    @Override
    public void onDisable() {

    }

    public String getPrefix(){
        return getConfig().getString("Plugin_Prefix");
    }

    public String getNoPermissionMessage(){
        return getConfig().getString("NoPermissionMessage");
    }

    public MarketHandler getMarketHandler(){
        return marketHandler;
    }

}
