package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public class ResetGlobalsCommand {
    public static int run(CommandContext<ServerCommandSource> context) {
        TITANOMACH_CONFIG.getTaskManager().resetGlobalTasks();
        return 0;
    }
}
