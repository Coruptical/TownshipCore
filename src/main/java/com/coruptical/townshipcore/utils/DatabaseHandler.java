package com.coruptical.townshipcore.utils;
import com.coruptical.townshipcore.TownshipCore;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

public class DatabaseHandler {
    // Constants
    private static final String DB_NAME = "data";
    private final String TABLEINFOSCHEMA_TABLENAME = "table_information_schema";
    private static final DatabaseHandler instance = new DatabaseHandler();

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
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create data.db file!");
            }
            createTableSchemaTable();
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

    // Utility methods and others
    public boolean tableExists(Connection connection, String tableName) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT count(*) "
            + "FROM " + TABLEINFOSCHEMA_TABLENAME
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

    public boolean createTable(Connection connection, String tableName, String parameters) {
        if (tableExists(connection, tableName)){
            return false;
        }

        try {
            Statement statement = connection.createStatement();
            String sql = String.format(
                    "CREATE TABLE %s ( " + parameters + " ); INSERT INTO %s (table_name) VALUES(%s)",
                    tableName, TABLEINFOSCHEMA_TABLENAME, tableName
            );
            statement.execute(sql);
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
                    "CREATE TABLE IF NOT EXISTS %s (id int NOT_NULL AUTO_INCREMENT, " +
                            "table_name, ",
                    TABLEINFOSCHEMA_TABLENAME
            );
            statement.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "There was an error while creating the table schema database table.");
            throw new RuntimeException(e);
        }
    }
}
