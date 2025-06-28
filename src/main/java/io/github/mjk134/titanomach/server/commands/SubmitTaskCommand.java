package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.context.CommandContext;
import io.github.mjk134.titanomach.server.tasks.CollectionTask;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static io.github.mjk134.titanomach.Titanomach.TASK_MANAGER;

public class SubmitTaskCommand {
    public static int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            return -1;
        }
        TASK_MANAGER.submitTask("test", player);
        return 0;
    }
}

