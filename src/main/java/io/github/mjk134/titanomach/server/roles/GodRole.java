package io.github.mjk134.titanomach.server.roles;

public class GodRole extends Role {
    public GodRole() {
        super("God", "filler", "minecraft:command_block", "§c", 100000);
        addRankUpReward("minecraft:netherite_ingot", 64);
        addEffect("minecraft:speed", 2);
        addEffect("minecraft:regeneration", 2);
        addEffect("minecraft:fire_resistance", 2);
        addEffect("minecraft:saturation", 1);
        addEffect("minecraft:water_breathing", 1);
        addEffect("minecraft:health_boost", 5);
        addEffect("minecraft:resistance", 4);
    }
}
