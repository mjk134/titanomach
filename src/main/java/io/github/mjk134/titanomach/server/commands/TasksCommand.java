package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class TasksCommand {
    public static int run(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null) {
            context.getSource().sendFeedback(() -> Text.literal("Only allowed to be run with a server player context."), false);
            return 1;
        }

        player.sendMessage(Text.literal("Task menu opened."), false);
        return 0;
    }
}
