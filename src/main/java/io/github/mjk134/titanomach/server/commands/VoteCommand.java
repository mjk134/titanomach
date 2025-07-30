package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.context.CommandContext;
import io.github.mjk134.titanomach.server.menu.VoteMenu;
import io.github.mjk134.titanomach.server.sacrifice.VoteManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class VoteCommand {

    public static int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        // Server check
        if (player == null) {
            context.getSource().sendFeedback(() -> Text.of("This command doesn't work on a server."), false);
            return -1;
        }

        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            if (!VoteManager.meetsRequirements(context.getSource().getServer().getPlayerManager())) {
                // feedback
                context.getSource().sendFeedback(() -> Text.of("There aren't enough players online for a vote to occur."), false);
                return -1;
            }
        }

        if (VoteManager.sacrificedItemPlayers.contains(player.getUuid())) {
            context.getSource().sendFeedback(() -> Text.of("You are already exempt from the vote."), false);
            return -1;
        }

        if (VoteManager.playerVoteMap.containsKey(player.getUuid())) {
            context.getSource().sendFeedback(() -> Text.of("You have already voted."), false);
            return -1;
        }

        VoteMenu menu = new VoteMenu(player);
        menu.displayTo(player);
        return 0;
    }
}
