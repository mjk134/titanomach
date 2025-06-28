package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

import static io.github.mjk134.titanomach.Titanomach.MOD_LOGGER;
import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public class DebugStartCommand {
    public static int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
        Boolean hasStarted = TITANOMACH_CONFIG.isStarted();

        assert sourcePlayer != null;

        if (hasStarted) {
            sourcePlayer.sendMessage(Text.literal("[Titanomach] Its already begun!!"));
            return 1;
        }

        sourcePlayer.sendMessage(Text.literal("Titanomach has been started."));
        TITANOMACH_CONFIG.setEnabled(true);
        TITANOMACH_CONFIG.start();
        for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
            player.networkHandler.disconnect(Text.literal("The Titanomachy has begun."));
        }

        return 0;
    }
}
