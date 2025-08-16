package io.github.mjk134.titanomach.server.roles;

public class KingRole extends Role {
    public KingRole() {
        super("King", "filler", "minecraft:golden_helmet", "§6", 50000);
        addRankUpReward("minecraft:beacon", 1);
        addRankUpReward("minecraft:end_crystal", 4);
        addEffect("minecraft:strength", 2);
        addEffect("minecraft:regeneration", 1);
    }
}
