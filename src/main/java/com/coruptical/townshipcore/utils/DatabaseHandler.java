package com.coruptical.townshipcore.utils;
import com.coruptical.townshipcore.TownshipCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public class DatabaseHandler implements Listener {
    // Constants
    private static final String DB_NAME = "data";
    private final String TABLEINFOSCHEMA_TABLENAME = "table_information_schema";
    private static final DatabaseHandler instance = new DatabaseHandler();
    private final String NAMEUUID_TABLE_NAME = "NameUUID";
    private final String PLAYED_BEFORE_TABLE_NAME = "Player_PlayedBefore";

    // Need to be set
    Connection conn = null;
    private TownshipCore plugin = null;
    private File databaseFile = null;

    public static DatabaseHandler getInstance() {
        return instance;
    }

    private DatabaseHandler(){

    }

    // Main methods for interaction
    public void init(TownshipCore plugin){
        this.plugin = plugin;


        String databaseFileName = DB_NAME+".db";
        databaseFile = new File(plugin.getDataFolder(), databaseFileName);
        if (!databaseFile.exists()) {
            databaseFile.getParentFile().mkdirs();
            try {
                databaseFile.createNewFile();

                // Create needed tables
                createTableSchemaTable();
                createNameUUIDTable();
                createPlayedBeforeTable();

                // Register this class as an event listener
                registerAll();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create data.db file!");
            }
        }
    }

    private void registerAll(){
        plugin.getServer().getPluginManager().registerEvents(getInstance(), plugin);
    }

    private Connection getDatabaseConnection(){
        if (conn != null) return conn;

        try {
            String databaseFilePath = "jdbc:sqlite:"
                    + plugin.getDataFolder().getAbsolutePath() + "/" + DB_NAME+".db";
            conn = DriverManager.getConnection(databaseFilePath);
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.SEVERE,
                    "Error while getting database connection.");
            return null;
        }
    }

    public Connection getConnection(){
        conn = getDatabaseConnection();
        return conn;
    }

    // Utility methods and others
    public boolean tableExists(Connection connection, String tableName) {
        try {
            String sql = String.format("SELECT id FROM %s WHERE TableName=\"%s\"",
                    TABLEINFOSCHEMA_TABLENAME, tableName);

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) != 0;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "There was an internal error while checking while a database table exists...");
            throw new RuntimeException(e);
        }
    }

    public boolean createTable(Connection connection, String tableName, String parameters) {
        if (tableExists(connection, tableName)){
            return false;
        }

        try {
            Statement statement = connection.createStatement();
            String sqlCreate = String.format(
                    "CREATE TABLE %s ( " + parameters + " )",
                    tableName, TABLEINFOSCHEMA_TABLENAME, tableName
            );
            statement.execute(sqlCreate);

            String sqlInsertTable = String.format("INSERT INTO %s (TableName) VALUES(\"%s\")",
                    TABLEINFOSCHEMA_TABLENAME, tableName);
            statement.execute(sqlInsertTable);
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "There was an error while creating the table schema database table.");
            throw new RuntimeException(e);
        }
    }

    public void createTableSchemaTable () {
        Connection connection = getConnection();

        try {
            Statement statement = connection.createStatement();
            String sql = String.format(
                    "CREATE TABLE IF NOT EXISTS %s (id INTEGER NOT NULL PRIMARY KEY, " +
                            "TableName VARCHAR(255))",
                    TABLEINFOSCHEMA_TABLENAME
            );
            statement.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "There was an error while creating the table schema database table.");
            throw new RuntimeException(e);
        }
    }

    private void createNameUUIDTable(){
        Connection connection = getConnection();

        String parameters = "UUID varchar(255) NOT NULL PRIMARY KEY, " +
                "Name varchar(255), "
                + "CheckName varchar(255), "
                + "JoinTimestamp varchar(255)";
        boolean isCreated = createTable(connection, NAMEUUID_TABLE_NAME, parameters);
        if (isCreated){
            plugin.getLogger().log(Level.INFO, "Created name-uuid table in database.");
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

    public String getUUIDFromName(Connection connection, String name){
        try {
            String sql = String.format("SELECT UUID from %s WHERE CheckName=\"%s\"",
                    NAMEUUID_TABLE_NAME, name.toLowerCase());

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString(1);
        } catch (SQLException e){
            plugin.getLogger().log(Level.SEVERE, "Error while getting player UUID from name in database.");
            throw new RuntimeException(e);
        }
    }

    // Create table to check if a player has joined before
    private void createPlayedBeforeTable(){
        Connection connection = getConnection();

        String parameters = "id INTEGER NOT NULL PRIMARY KEY, " +
                "UUID varchar(255)";
        boolean isCreated = createTable(connection, PLAYED_BEFORE_TABLE_NAME, parameters);
        if (isCreated){
            plugin.getLogger().log(Level.INFO, "Created has played before table in database.");
        }
    }





    // Event handlers
    /**
     *  Join event for adding into UUID-name data.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Connection connection = getConnection();
        Player p = e.getPlayer();
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        String formattedTimestamp = localDateTime.format(timeFormatter);


        // Insert into has played before and insert into uuid-name table
        // or update the name into the uuid
        if (!hasPlayedBefore(connection, p.getUniqueId().toString())){

            String sqlInsertIntoPlayedBefore = String.format(
                    "INSERT INTO %s(UUID) VALUES (\"%s\")",
                    PLAYED_BEFORE_TABLE_NAME, p.getUniqueId());
            String sqlInsertIntoUUIDName = String.format(
                    "INSERT INTO %s(UUID, Name, CheckName, JoinTimestamp) VALUES(?, ?, ?, ?)",
                    NAMEUUID_TABLE_NAME);
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertIntoPlayedBefore);
                preparedStatement.executeUpdate();

                preparedStatement = connection.prepareStatement(sqlInsertIntoUUIDName);
                preparedStatement.setString(1, p.getUniqueId().toString());
                preparedStatement.setString(2, p.getName());
                preparedStatement.setString(3, p.getName().toLowerCase());
                preparedStatement.setString(4, formattedTimestamp);
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                plugin.getLogger().log(Level.SEVERE, "Error on player join creating player balance.");
                throw new RuntimeException(exception);
            }
        } else{
            String sqlUpdate = String.format("UPDATE %s SET Name=?, CheckName=?, JoinTimestamp=? WHERE UUID=?",
                    NAMEUUID_TABLE_NAME);
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate);
                preparedStatement.setString(1, p.getName());
                preparedStatement.setString(2, p.getName().toLowerCase());
                preparedStatement.setString(3, formattedTimestamp);
                preparedStatement.setString(4, p.getUniqueId().toString());
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
