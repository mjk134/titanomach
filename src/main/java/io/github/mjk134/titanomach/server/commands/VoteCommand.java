package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.context.CommandContext;
import io.github.mjk134.titanomach.server.menu.Menu;
import io.github.mjk134.titanomach.server.menu.TaskMenu;
import io.github.mjk134.titanomach.server.menu.VoteMenu;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class VoteCommand {

    public static int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        // Server check
        if (player == null) {
            return -1;
        }

        Menu menu = new VoteMenu(player);
        menu.displayTo(player);
        return 0;
    }
}
