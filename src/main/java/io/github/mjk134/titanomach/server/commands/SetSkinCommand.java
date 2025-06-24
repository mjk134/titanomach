package io.github.mjk134.titanomach.server.commands;

import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.mjk134.titanomach.server.entity.ServerTitanomachPlayer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public class SetSkinCommand {
    public static int run(CommandContext<ServerCommandSource> context) {
        String skinId = StringArgumentType.getString(context, "skinId");
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (skinId == null) {
            return 1;
        }
        Property skinProperty = TITANOMACH_CONFIG.getSkinProperty(skinId);
        assert player != null;
        TITANOMACH_CONFIG.getPlayerConfig(player.getUuidAsString()).setSkinId(skinId);
        if (skinProperty == null && player == null) return 1;
        ((ServerTitanomachPlayer) player).titanomach$setSkin(skinProperty, true);
        return 0;
    }
}
