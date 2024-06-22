package com.coruptical.townshipcore.skill.stored_models;

import org.bukkit.ChatColor;
import org.bukkit.Material;

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

    public String getKeyName(){
        return ChatColor.stripColor(this.getName()).replace(" ", "_");
    }
}
