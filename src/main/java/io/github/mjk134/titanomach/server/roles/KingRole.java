package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.utils.ItemBuilder;

public class KingRole extends Role {
    public KingRole() {
        super("King", "filler", "minecraft:golden_helmet", "§6", 50000);
        addRankUpReward("minecraft:beacon", 1);
        addRankUpReward(new ItemBuilder("minecraft:end_crystal").setName("§fNetherite? Crystal").addLoreLine("§7These don't exist mate").setQuantity(4).create());
        addEffect("minecraft:strength", 2);
        addEffect("minecraft:regeneration", 1);
    }
}
