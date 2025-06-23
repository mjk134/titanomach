package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.mjk134.titanomach.utils.Skin;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Objects;

import static io.github.mjk134.titanomach.Titanomach.ModLogger;
import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public class AddSkinCommand {
    public static int run(CommandContext<ServerCommandSource> context) {
        String skinId = StringArgumentType.getString(context, "skinId");
        String texture = StringArgumentType.getString(context, "texture");
        String signature = StringArgumentType.getString(context, "signature");

        ModLogger.info("Adding skin, {} texture, {} signature, {}", skinId, texture, signature);
        TITANOMACH_CONFIG.addToSkinPool(new Skin(texture, signature, skinId));
        Objects.requireNonNull(context.getSource().getPlayer()).sendMessage(Text.literal("Skin successfully added! Check the logs for more information."));
        return 0;
    }
}
