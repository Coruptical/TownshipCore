package com.coruptical.townshipcore.economy;

import com.coruptical.townshipcore.economy.commands.BalanceCommand;
import com.coruptical.townshipcore.utils.DatabaseHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.logging.Level;

public class EconomyHandler implements Listener {
    private static final EconomyHandler instance = new EconomyHandler();
    private JavaPlugin plugin;
    private DatabaseHandler databaseHandler;
    private final String TABLE_NAME = "Player_Economy";
    private final String PLAYED_BEFORE_TABLE_NAME = "Player_PlayedBefore";
    public static final String ENUM_PREFIX = "economyhandler.";

    public static EconomyHandler getInstance() {
        return instance;
    }

    private EconomyHandler () {

    }

    public void init(JavaPlugin plugin, DatabaseHandler databaseHandler){
        this.plugin = plugin;
        this.databaseHandler = databaseHandler;
        createPlayedBeforeTable();
        registerAll();
    }

    // TODO: Make it so we don't output logger message if table already exists.
    public void createTable(){
        Connection connection = databaseHandler.getConnection();

        String parameters = "UUID varchar(255) NOT NULL PRIMARY KEY, " +
                        "balance DOUBLE";
        boolean isCreated = databaseHandler.createTable(connection, TABLE_NAME, parameters);
        if (isCreated){
            plugin.getLogger().log(Level.WARNING, "Created player economy table in database.");
        }

    }


    // Add player to economy table when they first join.
    // TODO: Add to player played before table
    @EventHandler
    private void onFirstJoin(PlayerJoinEvent e){
        Connection connection = databaseHandler.getConnection();
        Player p = e.getPlayer();
        if (!hasPlayedBefore(connection, p.getUniqueId().toString())){

            String sqlInsertIntoEconomy = String.format("INSERT INTO %s(UUID, balance) VALUES(?, ?)", TABLE_NAME);
            String sqlInsertIntoPlayedBefore = String.format(
                    "INSERT INTO %s(UUID) VALUES (\"%s\")",
                    PLAYED_BEFORE_TABLE_NAME, p.getUniqueId());
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertIntoEconomy);
                preparedStatement.setString(1, p.getUniqueId().toString());
                preparedStatement.setDouble(2, 0);
                preparedStatement.executeUpdate();

                preparedStatement = connection.prepareStatement(sqlInsertIntoPlayedBefore);
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                plugin.getLogger().log(Level.SEVERE, "Error on player join creating player balance.");
                throw new RuntimeException(exception);
            }
        }
    }

    public boolean hasPlayedBefore(Connection connection, String uuid){
        try {
            String sql = String.format("SELECT id FROM %s WHERE UUID=\"%s\"",
                    PLAYED_BEFORE_TABLE_NAME, uuid);

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) != 0;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while checking if player has played before in database.");
            throw new RuntimeException(e);
        }
    }

    // Create table to check if a player has joined before
    private void createPlayedBeforeTable(){
        Connection connection = databaseHandler.getConnection();

        String parameters = "id INTEGER NOT NULL PRIMARY KEY, " +
                "UUID varchar(255)";
        boolean isCreated = databaseHandler.createTable(connection, PLAYED_BEFORE_TABLE_NAME, parameters);
        if (isCreated){
            plugin.getLogger().log(Level.WARNING, "Created has played before table in database.");
        }
    }

    // Other utility methods
    private void registerAll(){
        // Register events
        plugin.getServer().getPluginManager().registerEvents(EconomyHandler.getInstance(), plugin);

        // Register commands
        BalanceCommand balanceCommand = new BalanceCommand();
        plugin.getCommand("balance").setExecutor(balanceCommand);
        plugin.getCommand("balance").setTabCompleter(balanceCommand);

    }

    // Front-end methods
    public double getBalance(String playerUUID){
        Connection connection = databaseHandler.getConnection();

        String sql = String.format("SELECT balance FROM %s WHERE UUID=?", TABLE_NAME);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, playerUUID);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getDouble(1);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while trying to fetch player balance.");
            throw new RuntimeException(e);
        }
    }

    public void setBalance(String playerUUID, double balance){
        Connection connection = databaseHandler.getConnection();

        String sql = String.format("UPDATE %s SET balance=? WHERE UUID=?", TABLE_NAME);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, balance);
            preparedStatement.setString(2, playerUUID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while trying to set player balance.");
            throw new RuntimeException(e);
        }

    }

}
