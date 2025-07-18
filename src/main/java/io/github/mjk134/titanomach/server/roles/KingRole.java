package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.utils.EffectUtil;
import io.github.mjk134.titanomach.utils.ItemBuilder;
import net.minecraft.entity.player.PlayerEntity;

public class KingRole extends Role {
    public KingRole() {
        super("King", "filler", "minecraft:golden_helmet", "§6", 50000);
        addRankUpReward("minecraft:beacon", 1);
        addRankUpReward(new ItemBuilder("minecraft:end_crystal").setName("§fNetherite? Crystal").addLoreLine("§7These don't exist mate").setQuantity(4).create());
    }

    @Override
    public void onEffectTick(PlayerEntity player) {
        EffectUtil.applyEffect(player, "minecraft:strength", RoleManager.EFFECT_INTERVAL_TICKS + 1, 2);
        EffectUtil.applyEffect(player, "minecraft:regeneration", RoleManager.EFFECT_INTERVAL_TICKS + 1, 1);
    }
}
