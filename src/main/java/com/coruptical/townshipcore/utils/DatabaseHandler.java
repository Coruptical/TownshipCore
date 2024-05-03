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
                createTableSchemaTable();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create data.db file!");
            }
        }

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


}
