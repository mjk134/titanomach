package io.github.mjk134.titanomach.server.roles;

public class NobleRole extends Role {
    public NobleRole() {
        super("Noble", "filler", "minecraft:diamond", "§b", 25000);
        addRankUpReward("minecraft:emerald", 64);
        addRankUpReward("minecraft:diamond", 32);
        addEffect("minecraft:speed", 1);
        addEffect("minecraft:resistance", 1);
    }
}
