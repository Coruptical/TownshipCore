package com.coruptical.townshipcore.economy.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class BalanceCommand implements CommandExecutor, TabCompleter {
    @Override
    // TODO: Use mojang api to get uuid, and if doesn't exist say in both cases.
    // TODO: Save name to uuid in database and update this every time a player joins in an async manner.
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender){
            if (args.length == 0) {
                sender.sendMessage("You must give a player name.");
                return true;
            }


        } else if (sender instanceof Player){

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
