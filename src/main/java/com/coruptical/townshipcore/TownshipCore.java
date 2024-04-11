package com.coruptical.townshipcore;

import com.coruptical.townshipcore.models.MarketItem;
import com.coruptical.townshipcore.utils.DatabaseHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class TownshipCore extends JavaPlugin {
    private static TownshipCore instance;
    private Economy economy;
    private final HashMap<String, MarketItem> marketItems = new HashMap<>();

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
    }

    @Override
    public void onDisable() {

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
