package io.github.mjk134.titanomach.server.roles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class FreemanRole extends Role {
    public FreemanRole() {
        super(
                "Freeman",
                "filler",
                "minecraft:orange_wool",
                "§6",
                6250
        );

        addRankUpReward("minecraft:golden_apple", 8);
        addRankUpReward("minecraft:diamond", 4);
        addEffect("minecraft:haste", 1);
        addEffect("minecraft:luck", 1);
    }

    @Override
    public void onEffectTick(PlayerEntity player) {
        // Remove the weakness from Peasant
        player.removeStatusEffect(Registries.STATUS_EFFECT.getEntry(Identifier.of("minecraft:weakness")).get());
        super.onEffectTick(player);
    }
}
