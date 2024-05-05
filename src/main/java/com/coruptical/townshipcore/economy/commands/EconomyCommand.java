package com.coruptical.townshipcore.economy.commands;

import com.coruptical.townshipcore.TownshipCore;
import com.coruptical.townshipcore.economy.EconomyHandler;
import com.coruptical.townshipcore.economy.enums.EconomyPermissions;
import com.coruptical.townshipcore.utils.DatabaseHandler;
import com.coruptical.townshipcore.utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class EconomyCommand implements CommandExecutor, TabCompleter {
    private final TownshipCore plugin = TownshipCore.getInstance();
    private final String USAGE_MESSAGE = "&7Incorrect usage: /eco <set/add/take/set-all> <player> <value>";

    // Usage: /eco <set/add/take/set-all> <offline? player> <value>
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (! (sender instanceof Player || sender instanceof ConsoleCommandSender))
            return true;

        if (!sender.hasPermission(EconomyPermissions.ECONOMY.getName())) {
            sender.sendMessage(Utils.pColor(plugin.getNoPermissionMessage()));
            return true;
        }

        handleCommand(sender, args);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }

    // Methods
    public void handleCommand(CommandSender sender, String[] args){
        if (args.length < 3){
            sender.sendMessage(Utils.pColor(USAGE_MESSAGE));
            return;
        }

        Connection connection = DatabaseHandler.getInstance().getConnection();

        String cmdOption = args[0];
        String playerName = args[1];
        String valueUnparsed = args[2];

        double value;
        try {
            value = Double.parseDouble(valueUnparsed);
            value = Utils.round(value);
        } catch (NumberFormatException e){
            sender.sendMessage(Utils.pColor("&8The value must be a number."));
            return;
        }

        if (cmdOption.equalsIgnoreCase("set") && playerName.equals("*")){
            handleSetAll(sender, value);
            return;
        }

        // TODO: Make * wild card work for add and take.

        String targetUUID = EconomyHandler.getInstance().getUUIDFromName(connection, playerName);
        if (targetUUID == null){
            sender.sendMessage(Utils.pColor("&8This player hasn't played before."));
            return;
        }
        handleOptions(sender, cmdOption, targetUUID, playerName, value);
    }

    public void handleSetAll(CommandSender sender, double value){
        EconomyHandler.getInstance().setAllBalance(value);
        sender.sendMessage(Utils.pColor(String.format("&aSuccessfully set all players' balance to &f%.2f.", Utils.round(value))));
    }

    public void handleOptions(CommandSender sender, String cmdOption, String targetUUID, String playerName, double value){
        if (cmdOption.equalsIgnoreCase("set")){
            EconomyHandler.getInstance().setBalance(targetUUID, value);
            sender.sendMessage(Utils.pColor(String.format("&aSuccessfully set %s's balance to $&f%.2f.", playerName, value)));
        }
        else if (cmdOption.equalsIgnoreCase("add")){
            EconomyHandler.getInstance().setBalance(targetUUID, EconomyHandler.getInstance().getBalance(targetUUID) + value);
            sender.sendMessage(Utils.pColor(String.format("&aSuccessfully added $&f%.2f to %s's balance.", value, playerName)));
        }
        else if (cmdOption.equalsIgnoreCase("take")){
            EconomyHandler.getInstance().setBalance(targetUUID, EconomyHandler.getInstance().getBalance(targetUUID) - value);
            sender.sendMessage(Utils.pColor(String.format("&aSuccessfully removed $&f%.2f from %s's balance.", value, playerName)));
        } else {
            sender.sendMessage(Utils.pColor(USAGE_MESSAGE));
        }
    }

}
