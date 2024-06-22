package com.coruptical.townshipcore.economy;

import com.coruptical.townshipcore.economy.commands.*;
import com.coruptical.townshipcore.economy.event.WithdrawNoteUseEvent;
import com.coruptical.townshipcore.utils.DatabaseHandler;
import com.coruptical.townshipcore.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;

public class EconomyHandler implements Listener {
    private static final EconomyHandler instance = new EconomyHandler();
    private JavaPlugin plugin;
    private DatabaseHandler databaseHandler;
    private final String TABLE_NAME = "Player_Economy";
    public static final String ENUM_PREFIX = "economyhandler.";
    private NamespacedKey withdrawNoteKey;
    private String noPermissionMessage;


    public static EconomyHandler getInstance() {
        return instance;
    }

    private EconomyHandler () {

    }

    public String getNoPermissionMessage() {
        return noPermissionMessage;
    }

    public NamespacedKey getWithdrawNoteKey() {
        return withdrawNoteKey;
    }

    public JavaPlugin getPlugin(){
        return plugin;
    }

    public void init(JavaPlugin plugin, DatabaseHandler databaseHandler, String noPermissionMessage){
        this.plugin = plugin;
        this.databaseHandler = databaseHandler;

        this.noPermissionMessage = noPermissionMessage;

        // Create our tables
        createPlayerEconomyTable();

        // Register commands and listeners
        registerAll();

        // Create namespaced key for withdraw paper note
        withdrawNoteKey = new NamespacedKey(plugin, "withdrawAmount");
    }

    public void createPlayerEconomyTable(){
        Connection connection = databaseHandler.getConnection();

        String parameters = "UUID varchar(255) NOT NULL PRIMARY KEY, " +
                        "balance DOUBLE";
        boolean isCreated = databaseHandler.createTable(connection, TABLE_NAME, parameters);
        if (isCreated){
            plugin.getLogger().log(Level.INFO, "Created player economy table in database.");
        }
    }






    // Other utility methods
    private void registerAll(){
        // Register events
        plugin.getServer().getPluginManager().registerEvents(EconomyHandler.getInstance(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new WithdrawNoteUseEvent(), plugin);

        // Register commands
        BalanceCommand balanceCommand = new BalanceCommand();
        plugin.getCommand("balance").setExecutor(balanceCommand);
        plugin.getCommand("balance").setTabCompleter(balanceCommand);

        EconomyCommand economyCommand = new EconomyCommand();
        plugin.getCommand("economy").setExecutor(economyCommand);
        plugin.getCommand("economy").setTabCompleter(economyCommand);

        BalanceTopCommand balanceTopCommand = new BalanceTopCommand();
        plugin.getCommand("balancetop").setExecutor(balanceTopCommand);

        WithdrawCommand withdrawCommand = new WithdrawCommand();
        plugin.getCommand("withdraw").setExecutor(withdrawCommand);

        PayCommand payCommand = new PayCommand();
        plugin.getCommand("pay").setExecutor(payCommand);

    }


    // Add player to economy table when they first join,
    // and add to uuid-name table in async manner.
    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        Connection connection = databaseHandler.getConnection();
        Player p = e.getPlayer();

        // Insert into has played before and insert into uuid-name table
        // or update the name into the uuid
        if (!databaseHandler.hasPlayedBefore(connection, p.getUniqueId().toString())) {

            String sqlInsertIntoEconomy = String.format("INSERT INTO %s(UUID, balance) VALUES(?, ?)", TABLE_NAME);
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertIntoEconomy);
                preparedStatement.setString(1, p.getUniqueId().toString());
                preparedStatement.setDouble(2, 0);
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                plugin.getLogger().log(Level.SEVERE, "Error on player join creating player balance.");
                throw new RuntimeException(exception);
            }
        }
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

    public String[] getAllPlayerUUIDS(){
        Connection connection = databaseHandler.getConnection();
        ArrayList<String> uuids = new ArrayList<>();

        String sql = String.format("SELECT UUID FROM %s", TABLE_NAME);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                uuids.add(resultSet.getString(1));
            }
            return uuids.toArray(new String[0]);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while trying to fetch all player uuids.");
            throw new RuntimeException(e);
        }
    }

    public void setAllBalance(double balance){
        Connection connection = databaseHandler.getConnection();

        String sql = String.format("UPDATE %s SET balance=%.2f", TABLE_NAME, Utils.round(balance));
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while trying to set all players' balance.");
            throw new RuntimeException(e);
        }
    }

    public void takeAllBalance(double balance){
        Connection connection = databaseHandler.getConnection();

        String sql = String.format("UPDATE %s SET balance=balance - %.2f", TABLE_NAME, Utils.round(balance));
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while trying to take all players' balance.");
            throw new RuntimeException(e);
        }
    }

    public void addAllBalance(double balance){
        Connection connection = databaseHandler.getConnection();

        String sql = String.format("UPDATE %s SET balance=balance + %.2f", TABLE_NAME, Utils.round(balance));
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while trying to add to all players' balance.");
            throw new RuntimeException(e);
        }
    }

    public LinkedHashMap<String, Double> getBalanceTop(int pageNumber, int pageSize){
        LinkedHashMap<String, Double> balanceTop = new LinkedHashMap<>();
        Connection connection = databaseHandler.getConnection();

        String sql = String.format("SELECT * FROM %s ORDER BY balance DESC LIMIT %s OFFSET %s", TABLE_NAME, pageSize, (pageNumber-1)*pageSize);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                balanceTop.put(resultSet.getString(1), resultSet.getDouble(2));
            }

            return balanceTop;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while trying to add to all players' balance.");
            throw new RuntimeException(e);
        }
    }

}
