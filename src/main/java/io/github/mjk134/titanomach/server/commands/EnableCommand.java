package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.context.CommandContext;
import io.github.mjk134.titanomach.server.network.FakeTextDisplayHolder;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Objects;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public class EnableCommand {
    public static int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
        Boolean isEnabled = TITANOMACH_CONFIG.isEnabled();
        Boolean hasStarted = TITANOMACH_CONFIG.isStarted();
        assert sourcePlayer != null;
        if (!hasStarted) {
            sourcePlayer.sendMessage(Text.literal("[Titanomach] You must start it first!"));
            return 1;
        }
        if (isEnabled) {
            sourcePlayer.sendMessage(Text.literal("Titanomach has already been enabled."));
            return 1;
        }
        PlayerManager playerManager = Objects.requireNonNull(sourcePlayer.getServer()).getPlayerManager();

        TITANOMACH_CONFIG.setEnabled(true);

        for (ServerPlayerEntity serverPlayer : playerManager.getPlayerList()) {
            serverPlayer.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, serverPlayer));
            ((FakeTextDisplayHolder) serverPlayer).titanomach$updateName();
        }

        sourcePlayer.sendMessage(Text.literal("Titanomach has been enabled."));
        return 0;
    }
}
