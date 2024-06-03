package com.coruptical.townshipcore.gui.impl;

import com.coruptical.townshipcore.TownshipCore;
import com.coruptical.townshipcore.gui.InventoryButton;
import com.coruptical.townshipcore.gui.InventoryGUI;
import com.coruptical.townshipcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class UserMarketInventory extends InventoryGUI {
    private final TownshipCore plugin = TownshipCore.getInstance();

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 9,
                Utils.color("&7[&bMarket&7]"));

    }

    @Override
    public void decorate(Player p){
        HashMap<Integer, InventoryButton> buttonHashMap;

        buttonHashMap = createButtons(p);

        for (int slot : buttonHashMap.keySet()){
            this.addButton(slot, buttonHashMap.get(slot));
        }

        super.decorate(p);
    }

    private HashMap<Integer, InventoryButton> createButtons(Player p){
        HashMap<Integer, InventoryButton> buttonHashMap = new HashMap<>();

        int count = 0;

        return buttonHashMap;
    }

    private ItemStack createItem(Material material, String name, String[] lore){
        ItemStack stack = new ItemStack(material, 1);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(Utils.color(name));

        ArrayList<String> coloredLore = new ArrayList<>();

        for (String string : lore){
            coloredLore.add(Utils.color(string));
        }

        meta.setLore(coloredLore);

        stack.setItemMeta(meta);

        return stack;
    }
}
