package io.github.mjk134.titanomach.utils;

import io.github.mjk134.titanomach.server.roles.RoleManager;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class EffectUtil {
    public static void applyEffect(PlayerEntity player, String id, int durationTicks, int level) {
        player.addStatusEffect(
                new StatusEffectInstance(
                        Registries.STATUS_EFFECT.getEntry(Identifier.of(id)).get(),
                        durationTicks,
                        level - 1
                )
        );
    }
}
