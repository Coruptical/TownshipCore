package com.coruptical.townshipcore.skill.commands;

import com.coruptical.townshipcore.gui.GUIManager;
import com.coruptical.townshipcore.gui.impl.SkillUserInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoSkillCommand implements CommandExecutor {
    private GUIManager guiManager;

    public InfoSkillCommand(GUIManager guiManager){
        this.guiManager = guiManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        this.guiManager.openGUI(new SkillUserInventory(), (Player) sender);
        return true;
    }
}
