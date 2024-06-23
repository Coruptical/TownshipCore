package com.coruptical.townshipcore.skill;

import com.coruptical.townshipcore.skill.commands.SkillManagerCommand;
import com.coruptical.townshipcore.skill.stored_models.Skill;
import com.coruptical.townshipcore.utils.DatabaseHandler;
import com.coruptical.townshipcore.utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class SkillManager {
    private JavaPlugin plugin;
    private String messagePrefix;
    private DatabaseHandler databaseHandler;
    private final String TABLE_NAME = "Player_Skill";
    private String noPermissionMessage = "";

    private SkillManager(){

    }

    private static SkillManager instance = new SkillManager();

    public static SkillManager getInstance() {
        return instance;
    }

    public void setNoPermissionMessage(String noPermissionMessage) {
        this.noPermissionMessage = noPermissionMessage;
    }

    public String getNoPermissionMessage() {
        return noPermissionMessage;
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
        registerAll();
    }

    private void registerAll(){
        SkillManagerCommand skillManagerCommand = new SkillManagerCommand();
        plugin.getCommand("skillmanager").setExecutor(skillManagerCommand);
        plugin.getCommand("skillmanager").setTabCompleter(skillManagerCommand);
    }

    private String getSkillsAsParameter(){
        StringBuilder stringBuilder = new StringBuilder();
        for (Skill skill : Skill.values()){
            stringBuilder.append(skill.toString() + "Level INT,");
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

    public void setAllSkillLevel(Skill skill, int value){
        Connection connection = databaseHandler.getConnection();

        String sql = String.format("UPDATE %s SET %s=%s", TABLE_NAME, skill.toString() + "Level", value);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while trying to set all players' skill level.");
            throw new RuntimeException(e);
        }
    }

    public void addAllSkillLevel(Skill skill, int value){
        Connection connection = databaseHandler.getConnection();
        String skillName = skill.toString() + "Level";

        String sql = String.format("UPDATE %s SET %s=%s + %s", TABLE_NAME, skillName, skillName, value);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while trying to add to all players' skill level.");
            throw new RuntimeException(e);
        }
    }

    public void takeAllSkillLevel(Skill skill, int value){
        Connection connection = databaseHandler.getConnection();
        String skillName = skill.toString() + "Level";

        String sql = String.format("UPDATE %s SET %s=%s - %s", TABLE_NAME, skillName, skillName, value);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while trying to remove from all players' skill level.");
            throw new RuntimeException(e);
        }
    }

    public void normaliseLevels(){
        Connection connection = databaseHandler.getConnection();

        // If any skill value less than 0, set to 0.
        for (Skill skill : Skill.values()){
            String skillName = skill.toString() + "Level";
            String sql = String.format("UPDATE %s SET %s=0 WHERE %s < 0", TABLE_NAME, skillName, skillName);
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while trying to add to all players' skill level.");
                throw new RuntimeException(e);
            }
        }

        // If any skill value more than 50, set to 50.
        for (Skill skill : Skill.values()){
            String skillName = skill.toString() + "Level";
            String sql = String.format("UPDATE %s SET %s=50 WHERE %s > 50", TABLE_NAME, skillName, skillName);
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while trying to add to all players' skill level.");
                throw new RuntimeException(e);
            }
        }
    }

    public void setSkillLevel(String playerUUID, Skill skill, int value){
        Connection connection = databaseHandler.getConnection();

        String sql = String.format("UPDATE %s SET %s=? WHERE UUID=?", TABLE_NAME, skill.toString() + "Level");
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, value);
            preparedStatement.setString(2, playerUUID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while trying to set player skill level.");
            throw new RuntimeException(e);
        }
    }

    public void takeSkillLevel(String playerUUID, Skill skill, int value){
        Connection connection = databaseHandler.getConnection();
        String skillName = skill.toString() + "Level";

        String sql = String.format("UPDATE %s SET %s=%s - ? WHERE UUID=?", TABLE_NAME, skillName, skillName);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, value);
            preparedStatement.setString(2, playerUUID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while trying to take from player skill level.");
            throw new RuntimeException(e);
        }
    }

    public int getSkillLevel(String playerUUID, Skill skill){
        Connection connection = databaseHandler.getConnection();

        String sql = String.format("SELECT %s FROM %s WHERE UUID=?", skill.toString()+"Level", TABLE_NAME);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, playerUUID);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while trying to fetch player balance.");
            throw new RuntimeException(e);
        }
    }

    public void addSkillLevel(String playerUUID, Skill skill, int value){
        Connection connection = databaseHandler.getConnection();
        String skillName = skill.toString() + "Level";

        String sql = String.format("UPDATE %s SET %s=%s + ? WHERE UUID=?", TABLE_NAME, skillName, skillName);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, value);
            preparedStatement.setString(2, playerUUID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while trying to add to player skill level.");
            throw new RuntimeException(e);
        }
    }
}
