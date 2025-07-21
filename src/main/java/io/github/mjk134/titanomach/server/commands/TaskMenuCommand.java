package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.context.CommandContext;
import io.github.mjk134.titanomach.server.menu.Menu;
import io.github.mjk134.titanomach.server.menu.TaskMenu;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class TaskMenuCommand {
    public static int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            return -1;
        }

        Menu menu = new TaskMenu(context.getSource().getPlayer());
        menu.displayTo(player);
        return 0;
    }
}
