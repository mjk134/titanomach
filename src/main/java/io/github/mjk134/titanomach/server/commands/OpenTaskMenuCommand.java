package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.context.CommandContext;
import io.github.mjk134.titanomach.server.TitanomachPlayer;
import io.github.mjk134.titanomach.server.tasks.CollectionTask;
import net.minecraft.server.command.ServerCommandSource;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public class OpenTaskMenuCommand{
    public static int run(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        TitanomachPlayer player = TITANOMACH_CONFIG.getPlayerConfig(source.getPlayer().getUuidAsString());

        if (player.task instanceof CollectionTask) {
            player.task.OpenTaskMenu(source.getPlayer());
        }
        return 0;
    }
}
