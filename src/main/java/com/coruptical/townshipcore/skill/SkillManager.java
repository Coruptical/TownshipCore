package com.coruptical.townshipcore.skill;

import org.bukkit.plugin.java.JavaPlugin;

public class SkillManager {
    private JavaPlugin plugin;

    private SkillManager(){

    }

    private static SkillManager instance = new SkillManager();

    public static SkillManager getInstance() {
        return instance;
    }

    /**
     * This method should only be run on start-up.
     */
    public void init(JavaPlugin plugin){
        this.plugin = plugin;
    }
}
