package com.coruptical.townshipcore;

import com.coruptical.townshipcore.models.MarketItem;
import com.coruptical.townshipcore.utils.DatabaseHandler;
import com.coruptical.townshipcore.utils.MarketHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Level;

public final class TownshipCore extends JavaPlugin {
    private static TownshipCore instance;
    private Economy economy;
    private final HashMap<String, MarketItem> marketItems = new HashMap<>();
    private MarketHandler marketHandler;

    public HashMap<String, MarketItem> getMarketItems() {
        return marketItems;
    }

    public Economy getEconomy() {
        return economy;
    }

    public static TownshipCore getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Initialise database and create file
        DatabaseHandler.getInstance().init(instance);
    
        // Attempt to set up economy
        // If this fails, the plugin disables itself.
        if (!setupEconomy(this)) {
            getLogger().log(Level.SEVERE, "Economy failed to setup. Disabling TownshipCore.java...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Set up market handler and market items
        marketHandler = MarketHandler.getInstance();
        marketHandler.init(this);
        marketHandler.createCustomConfig();


    }

    @Override
    public void onDisable() {

    }

    public MarketHandler getMarketHandler(){
        return marketHandler;
    }

    // Utility methods
    private boolean setupEconomy(TownshipCore plugin) {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }
}
