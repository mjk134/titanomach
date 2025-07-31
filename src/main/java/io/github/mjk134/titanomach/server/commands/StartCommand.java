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
import static io.github.mjk134.titanomach.Titanomach.CONFIG;

public class StartCommand {
    public static int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
        Boolean hasStarted = CONFIG.isStarted();

        assert sourcePlayer != null;

        if (hasStarted) {
            sourcePlayer.sendMessage(Text.literal("[Titanomach] Its already begun!!"));
            return 1;
        }

        sourcePlayer.sendMessage(Text.literal("Titanomach has been started."));
        PlayerManager playerManager = Objects.requireNonNull(sourcePlayer.getServer()).getPlayerManager();
        playerManager.sendToAll(new TitleS2CPacket(Text.literal("The Titanomachy has begun.")));
        Vec3d position = context.getSource().getPosition();
        context.getSource().getWorld().playSound(
                null,
                position.x,
                position.y,
                position.z,
                SoundEvents.ENTITY_ENDER_DRAGON_DEATH,
                SoundCategory.AMBIENT,
                1.0f,
                1.0f
        );

        for (int i = 10; i > 0; i--) {
            playerManager.sendToAll(new TitleS2CPacket(Text.literal("The Titanomachy has begun.")));
            playerManager.sendToAll(new SubtitleS2CPacket(Text.literal("You will be kicked in " + i + " seconds.")));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                MOD_LOGGER.error(e.getMessage());
            }
        }
        CONFIG.setEnabled(true);
        CONFIG.start();
        for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
            player.networkHandler.disconnect(Text.literal("The Titanomachy has begun."));
        }

        return 0;
    }
}
