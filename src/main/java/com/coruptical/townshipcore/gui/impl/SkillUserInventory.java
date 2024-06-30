package com.coruptical.townshipcore.gui.impl;

import com.coruptical.townshipcore.gui.InventoryButton;
import com.coruptical.townshipcore.gui.InventoryGUI;
import com.coruptical.townshipcore.skill.SkillManager;
import com.coruptical.townshipcore.skill.stored_models.Skill;
import com.coruptical.townshipcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class SkillUserInventory extends InventoryGUI {
    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 9, Utils.color("&7[&6Your Skills&7]"));
    }

    @Override
    public void decorate(Player player) {
        HashMap<Integer, InventoryButton> buttonHashMap;

        buttonHashMap = createButtons(player);

        for (int slot : buttonHashMap.keySet()){
            this.addButton(slot, buttonHashMap.get(slot));
        }

        super.decorate(player);
    }

    private HashMap<Integer, InventoryButton> createButtons(Player p){
        HashMap<Integer, InventoryButton> buttonHashMap = new HashMap<>();
        SkillManager skillManager = SkillManager.getInstance();

        int slot = 0;
        for (Skill skill : Skill.values()){
            int currentLevel = skillManager.getSkillLevel(p.getUniqueId().toString(), skill);
            InventoryButton button = new InventoryButton()
                    .creator(player -> skill.skillModel.createGuiItem(
                            player.getUniqueId().toString(), skill, currentLevel))
                    .consumer(event -> {
                    });

            buttonHashMap.put(slot, button);
            slot++;
        }
        return buttonHashMap;
    }
}
