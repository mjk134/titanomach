package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public interface CommandInterface {
    public static int run(CommandContext<ServerCommandSource> context) {
        return 0;
    }
}
