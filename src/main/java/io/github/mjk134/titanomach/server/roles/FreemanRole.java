package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.utils.EffectUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class FreemanRole extends Role {
    public FreemanRole() {
        super("Freeman", "filler", "minecraft:orange_wool", "§6", 6250);
    }

    @Override
    public void onEffectTick(PlayerEntity player) {
        // Remove the weakness from Peasant
        player.removeStatusEffect(Registries.STATUS_EFFECT.getEntry(Identifier.of("minecraft:weakness")).get());
        EffectUtil.applyEffect(player, "minecraft:haste", RoleManager.EFFECT_INTERVAL_TICKS + 1, 1);
        EffectUtil.applyEffect(player, "minecraft:luck", RoleManager.EFFECT_INTERVAL_TICKS + 1, 1);
    }
}
