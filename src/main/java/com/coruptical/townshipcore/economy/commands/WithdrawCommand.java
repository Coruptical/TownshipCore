package com.coruptical.townshipcore.economy.commands;

import com.coruptical.townshipcore.TownshipCore;
import com.coruptical.townshipcore.economy.EconomyHandler;
import com.coruptical.townshipcore.economy.enums.EconomyPermissions;
import com.coruptical.townshipcore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class WithdrawCommand implements CommandExecutor {
    private final TownshipCore plugin = TownshipCore.getInstance();
    private final String USAGE_MESSAGE = "&8Incorrect usage: &7/withdraw <amount>";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if (!p.hasPermission(EconomyPermissions.WITHDRAW.getName())) {
            p.sendMessage(Utils.pColor(plugin.getNoPermissionMessage()));
            return true;
        }

        handleCommand(p, args);


        return true;
    }

    private void handleCommand(Player p, String[] args) {
        double amount;
        if (args.length == 0) {
            p.sendMessage(Utils.pColor(USAGE_MESSAGE));
            return;
        }

        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException e){
            p.sendMessage(Utils.pColor("&7Invalid syntax, amount has to be a number."));
            return;
        }

        amount = Utils.round(amount);

        String uuid = p.getUniqueId().toString();
        double playersBalance = EconomyHandler.getInstance().getBalance(uuid);
        if (amount > playersBalance){
            p.sendMessage(Utils.pColor("&7You cannot withdraw this much."));
            return;
        }


        // TODO: Give the paper item we can get money from and add functionality

        if (p.getInventory().firstEmpty() == -1){
            p.sendMessage(Utils.pColor("&7You have no inventory space to withdraw money."));
            return;
        }

        ItemStack note = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = note.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        pdc.set(EconomyHandler.getInstance().getWithdrawNoteKey(), PersistentDataType.DOUBLE, amount);

        meta.setDisplayName(Utils.color("&7[&6Banknote&7]"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Utils.color(String.format("&aAmount: &f$%.2f", amount)));
        lore.add(Utils.color("&7Right-click to redeem 1"));
        lore.add(Utils.color("&7Shift + Right-click to redeem entire stack"));
        meta.setLore(lore);

        note.setItemMeta(meta);

        p.getInventory().addItem(note);
        EconomyHandler.getInstance().setBalance(uuid, playersBalance - amount);
        p.sendMessage(Utils.pColor(String.format("&aSuccessfully withdrew &f$%.2f&a from your balance.", amount)));
    }
}
