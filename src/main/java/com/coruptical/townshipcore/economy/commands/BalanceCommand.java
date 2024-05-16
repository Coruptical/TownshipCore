package com.coruptical.townshipcore.economy.commands;

import com.coruptical.townshipcore.TownshipCore;
import com.coruptical.townshipcore.economy.EconomyHandler;
import com.coruptical.townshipcore.economy.enums.EconomyPermissions;
import com.coruptical.townshipcore.utils.DatabaseHandler;
import com.coruptical.townshipcore.utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.util.List;

public class BalanceCommand implements CommandExecutor, TabCompleter {
    private final TownshipCore plugin = TownshipCore.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender){
            if (args.length == 0) {
                sender.sendMessage("You must give a player name.");
                return true;
            }

            Connection connection = DatabaseHandler.getInstance().getConnection();
            String playerName = args[0].toLowerCase();

            String uuid = EconomyHandler.getInstance().getUUIDFromName(connection, playerName);
            if (uuid == null){
                sender.sendMessage("This player has not played before.");
                return true;
            }

            double balance = EconomyHandler.getInstance().getBalance(uuid);
            sender.sendMessage(String.format("%s's balance: $%.2f",
                    playerName, balance));
        } else if (sender instanceof Player){
            Player p = (Player) sender;

            if (!p.hasPermission(EconomyPermissions.BALANCE.getName())){
                p.sendMessage(Utils.pColor(plugin.getNoPermissionMessage()));
                return true;
            }

            Connection connection = DatabaseHandler.getInstance().getConnection();

            if (args.length == 0) {

                String uuid = EconomyHandler.getInstance().getUUIDFromName(connection, p.getName());

                double balance = EconomyHandler.getInstance().getBalance(uuid);
                p.sendMessage(Utils.pColor(String.format("&aYour balance: &f$%.2f",
                        balance)));

                return true;
            }


            String playerName = args[0].toLowerCase();

            String uuid = EconomyHandler.getInstance().getUUIDFromName(connection, playerName);
            if (uuid == null){
                p.sendMessage(Utils.pColor("&8This player has not played before."));
                return true;
            }

            double balance = EconomyHandler.getInstance().getBalance(uuid);
            p.sendMessage(Utils.pColor(String.format("&a%s's balance: &f£%.2f",
                    args[0], balance)));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
