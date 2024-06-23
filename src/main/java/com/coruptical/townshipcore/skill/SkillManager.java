package com.coruptical.townshipcore.skill;

import com.coruptical.townshipcore.skill.stored_models.Skill;
import com.coruptical.townshipcore.utils.DatabaseHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.util.logging.Level;

public class SkillManager {
    private JavaPlugin plugin;
    private String messagePrefix;
    private DatabaseHandler databaseHandler;
    private final String TABLE_NAME = "Player_Skill";

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

        createPlayerSkillTable();
    }

    private String getSkillsAsParameter(){
        StringBuilder stringBuilder = new StringBuilder();
        for (Skill skill : Skill.values()){
            stringBuilder.append(skill.toString() + " INT,");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }

    /**
     * Creates the table that stores information about the level of players skills.
     */
    private void createPlayerSkillTable(){
        Connection connection = databaseHandler.getConnection();

        String parameters = "UUID varchar(255) NOT NULL PRIMARY KEY, " + getSkillsAsParameter();
        boolean isCreated = databaseHandler.createTable(connection, TABLE_NAME, parameters);
        if (isCreated){
            plugin.getLogger().log(Level.INFO, "Created player skill table in database.");
        }
    }
}
