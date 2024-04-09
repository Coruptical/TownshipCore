package com.coruptical.townshipcore.utils;
import com.coruptical.townshipcore.TownshipCore;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

    public void init(TownshipCore plugin){
        this.plugin = plugin;

        databaseFile = new File(plugin.getDataFolder().getAbsolutePath(), DB_NAME+".db");
        if (!databaseFile.exists()){
            try {
                databaseFile.createNewFile();
                plugin.getLogger().log(Level.WARNING,
                        "Database file created.");
            } catch (IOException e) {
                e.printStackTrace();
                plugin.getLogger().log(Level.SEVERE,
                        "File write error for database file.");
            }
        }
    }

    public Connection getConnection(){
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.SEVERE,
                    "Error while getting database connection.");

            return null;
        }
    }

    public void openConnection(){
        conn = getConnection();
        plugin.getLogger().log(Level.INFO,
                "Opened database connection.");
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
}
