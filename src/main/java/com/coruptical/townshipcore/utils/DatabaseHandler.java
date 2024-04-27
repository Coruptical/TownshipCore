package com.coruptical.townshipcore.utils;
import com.coruptical.townshipcore.TownshipCore;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

public class DatabaseHandler {
    private TownshipCore plugin = null;
    private static final DatabaseHandler instance = new DatabaseHandler();
    private static final String DB_NAME = "data";
    private File databaseFile = null;
    Connection conn = null;

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
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create data.db file!");
            }
        }

    }

    private Connection getDatabaseConnection(){
        if (conn != null) return conn;

        try {
            plugin.getLogger().log(Level.INFO,
                    "Opened database connection.");
            return DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getName());
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

    public boolean closeConnection(){
        try {
            conn.close();
            conn = null;
            plugin.getLogger().log(Level.INFO, "Closed database connection.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.SEVERE,
                    "Error caused while trying to close database connection.");
            return false;
        }
    }

    // Utility methods and others
    public boolean tableExists(Connection connection, String tableName) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT count(*) "
            + "FROM information_schema.tables "
            + "WHERE table_name = ?"
            + "LIMIT 1;");

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) != 0;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "There was an internal error while checking while a database table exists...");
            throw new RuntimeException(e);
        }
    }
}
