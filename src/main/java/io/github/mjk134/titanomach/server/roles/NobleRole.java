package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.utils.EffectUtil;
import net.minecraft.entity.player.PlayerEntity;

public class NobleRole extends Role {
    public NobleRole() {
        super("Noble", "filler", "minecraft:diamond", "§b", 25000);
        addRankUpReward("minecraft:emerald", 64);
        addRankUpReward("minecraft:diamond", 32);
    }

    @Override
    public void onEffectTick(PlayerEntity player) {
        EffectUtil.applyEffect(player, "minecraft:speed", RoleManager.EFFECT_INTERVAL_TICKS + 1, 1);
        EffectUtil.applyEffect(player, "minecraft:resistance", RoleManager.EFFECT_INTERVAL_TICKS + 1, 1);
    }
}
