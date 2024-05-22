package com.coruptical.townshipcore.economy.commands;


import com.coruptical.townshipcore.economy.EconomyHandler;
import com.coruptical.townshipcore.economy.enums.EconomyPermissions;
import com.coruptical.townshipcore.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {
    private final String USAGE_MESSAGE = "&8Incorrect usage: &7/pay <player> <amount>";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            return true;
        }

        if (!sender.hasPermission(EconomyPermissions.PAY.getName())){
            sender.sendMessage(Utils.pColor(EconomyHandler.getInstance().getNoPermissionMessage()));
            return true;
        }

        handleCommand((Player) sender, args);

        return true;
    }

    private void handleCommand(Player sender, String[] args){
        if (args.length < 2){
            sender.sendMessage(Utils.pColor(USAGE_MESSAGE));
            return;
        }

        Player player = EconomyHandler.getInstance().getPlugin().getServer().getPlayer(args[0]);
        if (player == null){
            sender.sendMessage(Utils.pColor("&7This player is invalid."));
            return;
        }
        if (!player.isOnline()){
            sender.sendMessage(Utils.pColor("&7This player is invalid."));
            return;
        }

        if (args[0].equalsIgnoreCase(sender.getName())){
            sender.sendMessage(Utils.pColor("&7You cannot pay yourself"));
            return;
        }

        double amount;
        try{
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e){
            sender.sendMessage(Utils.pColor("&7You must give the amount as a number."));
            return;
        }

        EconomyHandler economyHandler = EconomyHandler.getInstance();

        if (amount > economyHandler.getBalance(sender.getUniqueId().toString())){
            sender.sendMessage(Utils.pColor("&7You cannot send this much."));
            return;
        }

        economyHandler.setBalance(player.getUniqueId().toString(),
                economyHandler.getBalance(player.getUniqueId().toString()) + amount);

        economyHandler.setBalance(sender.getUniqueId().toString(),
                economyHandler.getBalance(sender.getUniqueId().toString()) - amount);

        sender.sendMessage(Utils.pColor(String.format("&aSuccessfully payed &f$%.2f&a to %s", amount, player.getName())));
        player.sendMessage(Utils.pColor(String.format("&aYou have received &f$%.2f&a from %s", amount, sender.getName())));
    }
}
