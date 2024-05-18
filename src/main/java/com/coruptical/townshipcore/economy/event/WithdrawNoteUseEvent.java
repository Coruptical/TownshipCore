package com.coruptical.townshipcore.economy.event;

import com.coruptical.townshipcore.economy.EconomyHandler;
import com.coruptical.townshipcore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class WithdrawNoteUseEvent implements Listener {

    @EventHandler
    public void onNoteUse(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if (!(e.getAction().equals(Action.RIGHT_CLICK_AIR)
            || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;
        if (!e.hasItem()) return;
        if (!p.getInventory().getItemInMainHand().getType().equals(Material.PAPER)) return;

        ItemStack note = p.getInventory().getItemInMainHand();
        PersistentDataContainer pdc = note.getItemMeta().getPersistentDataContainer();

        EconomyHandler economyHandler = EconomyHandler.getInstance();
        if (!pdc.has(economyHandler.getWithdrawNoteKey(), PersistentDataType.DOUBLE)) return;

        double amount = pdc.get(EconomyHandler.getInstance().getWithdrawNoteKey(),
                PersistentDataType.DOUBLE);

        boolean shifting = p.isSneaking();
        int itemAmount = note.getAmount();
        if (shifting) amount *= itemAmount;



        String uuid = p.getUniqueId().toString();
        economyHandler.setBalance(uuid, economyHandler.getBalance(uuid) + amount);

        if (shifting){
            note.setAmount(0);
        }else{
            note.setAmount(note.getAmount() - 1);
        }

        p.sendMessage(Utils.pColor(String.format("&aSuccessfully received &f$%.2f&a from banknote.", amount)));
    }
}
