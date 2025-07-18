package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.utils.EffectUtil;
import net.minecraft.entity.player.PlayerEntity;

public class GodRole extends Role {
    public GodRole() {
        super("God", "filler", "minecraft:command_block", "§c", 100000);
        addRankUpReward("minecraft:netherite_ingot", 64);
    }

    @Override
    public void onEffectTick(PlayerEntity player) {
        EffectUtil.applyEffect(player, "minecraft:speed", RoleManager.EFFECT_INTERVAL_TICKS + 1, 2);
        EffectUtil.applyEffect(player, "minecraft:regeneration", RoleManager.EFFECT_INTERVAL_TICKS + 1, 2);
        EffectUtil.applyEffect(player, "minecraft:fire_resistance", RoleManager.EFFECT_INTERVAL_TICKS + 1, 1);
        EffectUtil.applyEffect(player, "minecraft:saturation", RoleManager.EFFECT_INTERVAL_TICKS + 1, 1);
        EffectUtil.applyEffect(player, "minecraft:water_breathing", RoleManager.EFFECT_INTERVAL_TICKS + 1, 1);
        EffectUtil.applyEffect(player, "minecraft:health_boost", RoleManager.EFFECT_INTERVAL_TICKS + 1, 5);
        EffectUtil.applyEffect(player, "minecraft:resistance", RoleManager.EFFECT_INTERVAL_TICKS + 1, 4);
    }
}
