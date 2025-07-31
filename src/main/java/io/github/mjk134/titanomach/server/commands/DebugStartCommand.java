package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static io.github.mjk134.titanomach.Titanomach.CONFIG;

public class DebugStartCommand {
    public static int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
        Boolean hasStarted = CONFIG.isStarted();

        assert sourcePlayer != null;

        if (hasStarted) {
            sourcePlayer.sendMessage(Text.literal("[Titanomach] Its already begun!!"));
            return 1;
        }

        sourcePlayer.sendMessage(Text.literal("Titanomach has been started."));
        CONFIG.setEnabled(true);
        CONFIG.start();
        for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
            player.networkHandler.disconnect(Text.literal("The Titanomachy has begun."));
        }

        return 0;
    }
}
