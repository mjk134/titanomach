package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.context.CommandContext;
import io.github.mjk134.titanomach.server.tasks.SlayerTask;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public class AddTaskCommand {
public static int run(CommandContext<ServerCommandSource> context) {
    ServerPlayerEntity player = context.getSource().getPlayer();
    if (player == null) {
        return -1;
    }
    TITANOMACH_CONFIG.getTaskManager().addTask(new SlayerTask("test", 36 * 64, 100, "minecraft:zombie"), player.getUuidAsString());
    return 0;
}
}

