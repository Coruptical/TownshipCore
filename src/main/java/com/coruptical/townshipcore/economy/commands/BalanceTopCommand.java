package com.coruptical.townshipcore.economy.commands;

import com.coruptical.townshipcore.TownshipCore;
import com.coruptical.townshipcore.economy.enums.EconomyPermissions;
import com.coruptical.townshipcore.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BalanceTopCommand implements CommandExecutor {
    private final TownshipCore plugin = TownshipCore.getInstance();

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

    private void handleCommand(CommandSender sender, String[] args){
        
    }
}
