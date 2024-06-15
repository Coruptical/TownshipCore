package com.coruptical.townshipcore.skill.stored_models;

import com.coruptical.townshipcore.utils.Utils;
import org.bukkit.Material;

public enum Skill {
    TOKENMASTER(new SkillModel(
            Utils.color("&eToken Master"),
            Utils.color("&7Increases the number of tokens acquired per sugar cane."),
            0,
            Material.SUNFLOWER,
            0,
            0,
            0)),
    SPAWNERFINDER(new SkillModel(
            Utils.color("&5Spawner Finder"),
            Utils.color("&7Increases the chance of finding spawners when breaking sugar cane."),
            0,
            Material.SPAWNER,
            0,
            0,
            0
    )),
    KEYFINDER(new SkillModel(
            Utils.color("&6Key Finder"),
            Utils.color("&7Increases the chance of rolling a key when breaking sugar cane."),
            0,
            Material.TRIPWIRE_HOOK,
            0,
            0,
            0
    )),
    AUTOSELL(new SkillModel(
            Utils.color("&bAuto Sell"),
            Utils.color("&7Automatically sells sugar cane for you once you acquire enough."),
            1,
            Material.PAPER,
            0,
            0,
            0
    ))
    ;

    public final SkillModel skillModel;

    Skill(SkillModel skillModel) {
        this.skillModel = skillModel;
    }
}
