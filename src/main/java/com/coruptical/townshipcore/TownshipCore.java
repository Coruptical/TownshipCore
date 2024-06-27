package com.coruptical.townshipcore;
import com.coruptical.townshipcore.economy.EconomyHandler;
import com.coruptical.townshipcore.gui.GUIListener;
import com.coruptical.townshipcore.gui.GUIManager;
import com.coruptical.townshipcore.skill.SkillManager;
import com.coruptical.townshipcore.skill.commands.InfoSkillCommand;
import com.coruptical.townshipcore.utils.DatabaseHandler;
import com.coruptical.townshipcore.utils.MarketHandler;
import com.coruptical.townshipcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public final class TownshipCore extends JavaPlugin {
    private static TownshipCore instance;

    private MarketHandler marketHandler;
    private DatabaseHandler databaseHandler;
    private EconomyHandler economyHandler;
    private SkillManager skillManager;
    private GUIManager guiManager;

    public GUIManager getGuiManager() {
        return guiManager;
    }

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

        // Set up skill manager
        skillManager = SkillManager.getInstance();
        skillManager.init(instance);
        skillManager.setNoPermissionMessage(Utils.pColor(getNoPermissionMessage()));

        // Set up gui manager to handle inventories
        guiManager = new GUIManager();

        // Set up gui listener to listen for events and pass them to the responsible inventory.
        GUIListener guiListener = new GUIListener(guiManager);
        Bukkit.getPluginManager().registerEvents(guiListener, getInstance());

        // Register the command to open skills, using this plugin's gui manager.
        InfoSkillCommand infoSkillCommand = new InfoSkillCommand(getGuiManager());
        getCommand("skills").setExecutor(infoSkillCommand);
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
