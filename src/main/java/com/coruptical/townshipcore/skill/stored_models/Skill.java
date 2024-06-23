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
            200)
    );

    public final SkillModel skillModel;

    Skill(SkillModel skillModel) {
        this.skillModel = skillModel;
    }
}
