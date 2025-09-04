package io.github.mjk134.titanomach.server.commands;

import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.context.CommandContext;
import io.github.mjk134.titanomach.server.TitanomachPlayer;
import io.github.mjk134.titanomach.server.entity.ServerTitanomachPlayer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static io.github.mjk134.titanomach.Titanomach.CONFIG;

public class RefreshSkinCommand {

    public static int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        TitanomachPlayer titanomachPlayer = CONFIG.getPlayerConfig(player);
        titanomachPlayer.getRandomIdentity().setSkinId(CONFIG.getRandomSkin().getUuid());
        CONFIG.dump();
        Property property = CONFIG.getSkinProperty(titanomachPlayer.getRandomIdentity().getSkinId());
        ((ServerTitanomachPlayer) player).titanomach$setSkin(property, true);
        context.getSource().sendFeedback(() -> Text.of("Refreshed your skin."), false);
        return 0;
    }

}
