package io.github.mjk134.titanomach.server.roles;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class PeasantRole extends Role {
    public PeasantRole() {
        super(
                "Peasant",
                """

                        §7Filler blah blah blah
                        §7Applies §9Weakness I
                        """,
                "minecraft:wheat",
                "§7",
                0);
    }

    @Override
    public void onEffectTick(PlayerEntity player) {
        player.addStatusEffect(
                new StatusEffectInstance(
                        Registries.STATUS_EFFECT.getEntry(Identifier.of("minecraft:weakness")).get(),
                        RoleManager.EFFECT_INTERVAL_TICKS,
                        0
                )
        );
    }
}
