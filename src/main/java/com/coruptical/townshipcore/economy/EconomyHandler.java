package com.coruptical.townshipcore.economy;

import com.coruptical.townshipcore.utils.DatabaseHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class EconomyHandler {
    private static final EconomyHandler instance = new EconomyHandler();
    private JavaPlugin plugin;
    private DatabaseHandler databaseHandler;
    private final String TABLE_NAME = "Player_Economy";

    public static EconomyHandler getInstance() {
        return instance;
    }

    private EconomyHandler () {

    }

    public void init(JavaPlugin plugin, DatabaseHandler databaseHandler){
        this.plugin = plugin;
        this.databaseHandler = databaseHandler;
    }

    // TODO: Make it so we don't output logger message if table already exists.
    public void createTable(){
        Connection connection = databaseHandler.getConnection();

        String parameters = "UUID varchar(255), " +
                        "displayName varchar(255), " +
                        "balance DOUBLE";
        boolean isCreated = databaseHandler.createTable(connection, TABLE_NAME, parameters);
        if (isCreated){
            plugin.getLogger().log(Level.WARNING, "Created player economy table in database.");
        }

        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
