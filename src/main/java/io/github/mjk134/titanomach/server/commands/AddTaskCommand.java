package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.context.CommandContext;
import io.github.mjk134.titanomach.server.tasks.CollectionTask;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static io.github.mjk134.titanomach.Titanomach.CONFIG;

public class AddTaskCommand {
    public static int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            return -1;
        }
        CONFIG.getTaskManager().addTask(
                new CollectionTask("Dirt collection", 27 * 64, 1000, "minecraft:dirt"),
                player.getUuidAsString()
        );
        return 0;
    }
}

