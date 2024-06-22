package com.coruptical.townshipcore.skill;

import com.coruptical.townshipcore.utils.DatabaseHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class SkillManager {
    private JavaPlugin plugin;
    private String messagePrefix;
    private DatabaseHandler databaseHandler;

    private SkillManager(){

    }

    private static SkillManager instance = new SkillManager();

    public static SkillManager getInstance() {
        return instance;
    }

    /**
     * This method should only be run on start-up.
     * It is currently dependent on my economy handler, which contains a UUID-name conversion.
     */
    public void init(JavaPlugin plugin, String messagePrefix){
        this.plugin = plugin;
        this.messagePrefix = messagePrefix;
        this.databaseHandler = DatabaseHandler.getInstance();
    }
}
