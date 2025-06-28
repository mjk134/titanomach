package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.mjk134.titanomach.server.menu.ExampleMenu;
import io.github.mjk134.titanomach.server.tasks.CollectionTask;
import io.github.mjk134.titanomach.server.tasks.Task;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static io.github.mjk134.titanomach.Titanomach.TASK_MANAGER;

public class AddTaskCommand {
    public static int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            return -1;
        }
        TASK_MANAGER.addTask(new CollectionTask("test", 64, "minecraft:dirt"), player.getUuidAsString());
        return 0;
    }
}

