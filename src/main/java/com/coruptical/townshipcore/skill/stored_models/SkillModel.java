package com.coruptical.townshipcore.skill.stored_models;

import com.coruptical.townshipcore.skill.SkillManager;
import com.coruptical.townshipcore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class SkillModel {
    private String name;

    private String description;

    private Material guiMaterial;

    private int startingExp;

    private int maxLevel;

    public SkillModel(String name, String description, int maxLevel, Material guiMaterial, int startingExp){
        this.name = name;
        this.description = description;
        this.maxLevel = maxLevel;
        this.guiMaterial = guiMaterial;
        this.startingExp = startingExp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public Material getGuiMaterial() {
        return guiMaterial;
    }

    public void setGuiMaterial(Material guiMaterial) {
        this.guiMaterial = guiMaterial;
    }

    public int getStartingExp() {
        return startingExp;
    }

    public void setStartingExp(int startingExp) {
        this.startingExp = startingExp;
    }

    // Custom methods
    public ItemStack createGuiItem(String playerUUID, Skill skill, int currentLevel){
        ArrayList<String> lore = new ArrayList<>();
        lore.add(String.format("&7Your level: &f%s", currentLevel));

        long nextLevelCost = Math.round(getStartingExp()*Math.exp(0.12*currentLevel));
        long currentExperience = SkillManager.getInstance().getAmountExperience(playerUUID, skill);
        lore.add(String.format("&7Progression: &f%s/%s exp", currentExperience, nextLevelCost));


        String[] loreArray = new String[lore.size()];
        loreArray = lore.toArray(loreArray);
        return Utils.createItem(getGuiMaterial(), "&7[" + getName() + "&7]", loreArray);
    }
}
