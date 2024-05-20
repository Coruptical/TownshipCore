package com.coruptical.townshipcore.economy.commands;

import com.coruptical.townshipcore.TownshipCore;
import com.coruptical.townshipcore.economy.EconomyHandler;
import com.coruptical.townshipcore.economy.enums.EconomyPermissions;
import com.coruptical.townshipcore.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;

public class BalanceTopCommand implements CommandExecutor {
    private final String USAGE_MESSAGE = "&8Incorrect usage: &7/balancetop <?page>";
    private final int BALANCES_PER_PAGE = 15;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (! (sender instanceof Player || sender instanceof ConsoleCommandSender))
            return true;

        if (!sender.hasPermission(EconomyPermissions.BALANCETOP.getName())) {
            sender.sendMessage(Utils.pColor(EconomyHandler.getInstance().getNoPermissionMessage()));
            return true;
        }

        handleCommand(sender, args);

        return true;
    }

    private void handleCommand(CommandSender sender, String[] args){
        int pageNumber;

        if (args.length == 0){
            pageNumber = 1;
        }else{
            try{
                pageNumber = Integer.parseInt(args[0]);
            } catch (NumberFormatException e){
                sender.sendMessage(Utils.pColor(USAGE_MESSAGE));
                return;
            }
        }


        LinkedHashMap<String, Double> balanceTop = EconomyHandler.getInstance().getBalanceTop(pageNumber, BALANCES_PER_PAGE);


        JavaPlugin plugin = EconomyHandler.getInstance().getPlugin();
        StringBuilder msg = new StringBuilder();
        for (String key : balanceTop.keySet()){
            String playerName = plugin.getServer().getOfflinePlayer(UUID.fromString(key)).getName();
            double balance = balanceTop.get(key);

            String line = String.format("&a%s: &f$%.2f\n", playerName, balance);

            msg.append(line);
        }

        if (msg.length() > 0) msg.deleteCharAt(msg.length()-1);
        sender.sendMessage(Utils.pColor(String.format("&aBalance top %s: \n%s", pageNumber, msg)));
    }
}
