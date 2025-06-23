package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.context.CommandContext;
import io.github.mjk134.titanomach.server.network.FakeTextDisplayHolder;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.Objects;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public class DisableCommand implements CommandInterface {
    public static int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
        Boolean isEnabled = TITANOMACH_CONFIG.isEnabled();
        assert sourcePlayer != null;

        if (!isEnabled) {
            sourcePlayer.sendMessage(Text.literal("Titanomach has already been disabled."));
            return 0;
        }
        TITANOMACH_CONFIG.setEnabled(false);
        PlayerManager playerManager = Objects.requireNonNull(sourcePlayer.getServer()).getPlayerManager();
        for (ServerPlayerEntity serverPlayer : playerManager.getPlayerList()) {
            serverPlayer.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, serverPlayer));
            ((FakeTextDisplayHolder) serverPlayer).titanomach$updateName();
        }
        sourcePlayer.sendMessage(Text.literal("Titanomach has been disabled."));
        return 0;
    }
}
