package com.coruptical.townshipcore;

import com.coruptical.townshipcore.models.MarketItem;
import com.coruptical.townshipcore.utils.MarketHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class TownshipCore extends JavaPlugin {
    private static TownshipCore instance;
    private HashMap<String, MarketItem> marketItemMap = new HashMap<>();
    private MarketHandler marketHandler;

    public HashMap<String, MarketItem> getMarketItemMap() {
        return marketItemMap;
    }

    public static TownshipCore getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        
        // Set up market handler and market items
        marketHandler = MarketHandler.getInstance();
        marketHandler.init(this);
        marketHandler.createCustomConfig();

        marketItemMap = marketHandler.getMarketItems();
    }

    @Override
    public void onDisable() {

    }

    public MarketHandler getMarketHandler(){
        return marketHandler;
    }

}
