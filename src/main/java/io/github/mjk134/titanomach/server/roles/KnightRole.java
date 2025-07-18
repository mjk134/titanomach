package io.github.mjk134.titanomach.server.roles;

public class KnightRole extends Role{
    public KnightRole() {
        super("Knight", "filler", "minecraft:iron_sword", "§a", 12500);
        addRankUpReward("minecraft:netherite_upgrade_smithing_template", 1);
        addEffect("minecraft:strength", 1);
    }
}
