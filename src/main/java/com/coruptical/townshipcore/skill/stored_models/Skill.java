package com.coruptical.townshipcore.skill.stored_models;

import com.coruptical.townshipcore.utils.Utils;
import org.bukkit.Material;

public enum Skill {
    FARMING(new SkillModel(
            Utils.color("&eFarming"),
            Utils.color("&7Increases farming fortune"),
            50,
            Material.SUNFLOWER,
            500)),
    FORAGING(new SkillModel(
            Utils.color("&6Foraging"),
            Utils.color("&7Increases foraging fortune"),
            50,
            Material.DIAMOND_AXE,
            200)),
    MINING(new SkillModel(
            Utils.color("&7Mining"),
            Utils.color("&7Increases mining fortune"),
            50,
            Material.DIAMOND_PICKAXE,
            200)),
    COMBAT(new SkillModel(
            Utils.color("&cCombat"),
            Utils.color("&7Increases damage dealt to mobs"),
            30,
            Material.DIAMOND_SWORD,
            200
    )),
    FISHING(new SkillModel(
            Utils.color("&bFishing"),
            Utils.color("&7Increases fishing fortune and bonus health"),
            50,
            Material.FISHING_ROD,
            200
    )),
    ENCHANTING(new SkillModel(
            Utils.color("&dEnchanting"),
            Utils.color("&7Increases intelligence"),
            40,
            Material.ENCHANTING_TABLE,
            300
    ));

    public final SkillModel skillModel;

    Skill(SkillModel skillModel) {
        this.skillModel = skillModel;
    }
}
