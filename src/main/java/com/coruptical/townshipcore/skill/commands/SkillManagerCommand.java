package com.coruptical.townshipcore.skill.commands;

import com.coruptical.townshipcore.economy.EconomyHandler;
import com.coruptical.townshipcore.skill.SkillManager;
import com.coruptical.townshipcore.skill.stored_models.Skill;
import com.coruptical.townshipcore.utils.DatabaseHandler;
import com.coruptical.townshipcore.utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.util.List;

public class SkillManagerCommand implements CommandExecutor, TabCompleter {
    private final String USAGE_MESSAGE =
            "&8Incorrect usage: &7/skillmanager <set/add/take> <player> <skill> <value>";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player || sender instanceof ConsoleCommandSender))
            return true;

        if (!sender.hasPermission("skillmanager.admin")) {
            sender.sendMessage(Utils.pColor(SkillManager.getInstance().getNoPermissionMessage()));
            return true;
        }

        handleCommand(sender, args);
        return true;
    }

    public void handleCommand(CommandSender sender, String[] args){
        if (args.length < 4){
            sender.sendMessage(Utils.pColor(USAGE_MESSAGE));
            return;
        }

        Connection connection = DatabaseHandler.getInstance().getConnection();

        String cmdOption = args[0];
        String playerName = args[1];
        String skillOption = args[2];
        String valueUnparsed = args[3];

        int value;
        try {
            value = Integer.parseInt(valueUnparsed);
        } catch (NumberFormatException e){
            sender.sendMessage(Utils.pColor("&8The value must be a whole number."));
            return;
        }

        if (value < 0){
            sender.sendMessage(Utils.pColor("&7This value is invalid."));
            return;
        }

        Skill skill;
        try{
            skill = Skill.valueOf(skillOption.toUpperCase());
        } catch (IllegalArgumentException e){
            sender.sendMessage(Utils.pColor("&8This skill is not valid."));
            return;
        }

        if (!(cmdOption.equalsIgnoreCase("set") ||
                cmdOption.equalsIgnoreCase("add") ||
                cmdOption.equalsIgnoreCase("take"))){
            sender.sendMessage(Utils.pColor(USAGE_MESSAGE));
            return;
        }

        if (cmdOption.equalsIgnoreCase("set") && playerName.equals("*")){
            handleSetAll(sender, skill, value);
            return;
        }

        if  (cmdOption.equalsIgnoreCase("take") && playerName.equals("*")){
            handleTakeAll(sender, skill, value);
            return;
        }

        if (cmdOption.equalsIgnoreCase("add") && playerName.equalsIgnoreCase("*")){
            handleAddAll(sender, skill, value);
            return;
        }

        String targetUUID = DatabaseHandler.getInstance().getUUIDFromName(connection, playerName);
        if (targetUUID == null){
            sender.sendMessage(Utils.pColor("&8This player hasn't played before."));
            return;
        }
        handleOptions(sender, cmdOption, targetUUID, playerName, skill, value);
    }

    private void handleSetAll(CommandSender sender, Skill skill, int value){
        if (value > skill.skillModel.getMaxLevel()){
            sender.sendMessage(Utils.pColor("&7This value is too large."));
            return;
        }

        SkillManager.getInstance().setAllSkillLevel(skill, value);
        sender.sendMessage(Utils.pColor(String.format("&aSuccessfully set all players' %s skill to &f%s.", skill.toString().toLowerCase(), value)));
    }

    private void handleTakeAll(CommandSender sender, Skill skill, int value){
        SkillManager.getInstance().takeAllSkillLevel(skill, value);
        SkillManager.getInstance().normaliseLevels();
        sender.sendMessage(Utils.pColor(String.format("&aSuccessfully taken &f%s&a from all players' %s skill.", value, skill.toString().toLowerCase())));
    }

    private void handleAddAll(CommandSender sender, Skill skill, int value){
        SkillManager.getInstance().addAllSkillLevel(skill, value);
        SkillManager.getInstance().normaliseLevels();
        sender.sendMessage(Utils.pColor(String.format("&aSuccessfully added &f%s&a to all players' %s skill.", value, skill.toString().toLowerCase())));
    }

    private void handleOptions(CommandSender sender, String cmdOption, String targetUUID, String playerName, Skill skill, int value){
        if (cmdOption.equalsIgnoreCase("set")){
            if (value > skill.skillModel.getMaxLevel()){
                sender.sendMessage(Utils.pColor("&7This value is too large."));
                return;
            }

            SkillManager.getInstance().setSkillLevel(targetUUID, skill, value);
            sender.sendMessage(Utils.pColor(String.format("&aSuccessfully set %s's %s level to &f%s.", playerName, skill.toString().toLowerCase(), value)));
        }
        else if (cmdOption.equalsIgnoreCase("add")){
            if ((value + SkillManager.getInstance().getSkillLevel(targetUUID, skill)) > skill.skillModel.getMaxLevel()){
                sender.sendMessage(Utils.pColor("&7Cannot add this much."));
                return;
            }

            SkillManager.getInstance().addSkillLevel(targetUUID, skill, value);
            sender.sendMessage(Utils.pColor(String.format("&aSuccessfully added &f%s&a to %s's %s level", value, playerName, skill.toString().toLowerCase())));
        }
        else if (cmdOption.equalsIgnoreCase("take")){
            if ((SkillManager.getInstance().getSkillLevel(targetUUID, skill) - value) < 0){
                sender.sendMessage(Utils.pColor("&7Cannot take this much."));
                return;
            }

            SkillManager.getInstance().takeSkillLevel(targetUUID, skill, value);
            sender.sendMessage(Utils.pColor(String.format("&aSuccessfully removed &f%s&a from %s's %s level.", value, playerName, skill.toString().toLowerCase())));
        } else {
            sender.sendMessage(Utils.pColor(USAGE_MESSAGE));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
