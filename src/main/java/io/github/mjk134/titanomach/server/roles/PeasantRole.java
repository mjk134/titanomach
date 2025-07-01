package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.server.tasks.CollectionTask;
import io.github.mjk134.titanomach.server.tasks.Task;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class PeasantRole extends Role {
    public PeasantRole() {
        super(
                "Peasant",
                """

                        §7The downtrodden masses, fighting to survive.
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

    @Override
    public Task[] getGlobalTasks() {
        return new Task[] {
                new CollectionTask("Dirt collection", 27 * 64, 1000, true,"minecraft:dirt")
        };
    }
}
