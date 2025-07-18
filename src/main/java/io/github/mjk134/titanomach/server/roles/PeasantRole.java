package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.server.tasks.CollectionTask;
import io.github.mjk134.titanomach.server.tasks.Task;
import io.github.mjk134.titanomach.utils.EffectUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import static io.github.mjk134.titanomach.Titanomach.MOD_LOGGER;

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
        EffectUtil.applyEffect(player, "minecraft:weakness", RoleManager.EFFECT_INTERVAL_TICKS + 1, 1);
    }

    @Override
    public Task[] getGlobalTasks() {
        return new Task[] {};
    }
}
