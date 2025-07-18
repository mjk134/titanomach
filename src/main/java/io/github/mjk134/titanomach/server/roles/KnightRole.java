package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.utils.EffectUtil;
import net.minecraft.entity.player.PlayerEntity;

public class KnightRole extends Role{
    public KnightRole() {
        super("Knight", "filler", "minecraft:iron_sword", "§a", 12500);
        addRankUpReward("minecraft:netherite_upgrade_smithing_template", 1);
    }

    @Override
    public void onEffectTick(PlayerEntity player) {
        EffectUtil.applyEffect(player, "minecraft:strength", RoleManager.EFFECT_INTERVAL_TICKS + 1, 1);
    }
}
