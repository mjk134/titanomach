package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.mjk134.titanomach.Titanomach;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PPCommands {
    public static LiteralArgumentBuilder<ServerCommandSource> get() {
        return literal("pp")
            .requires(source -> source.hasPermissionLevel(4))
            .then(argument("target", EntityArgumentType.entity())
                .then(literal("set")
                    .then(argument("amount", IntegerArgumentType.integer())
                        .executes((context) -> {
                            Entity target = EntityArgumentType.getEntity(context, "target");
                            if (target instanceof ServerPlayerEntity player) {
                                Titanomach.CONFIG.getPlayerConfig(player).setProgressPoints(IntegerArgumentType.getInteger(context, "amount"));
                            } else {
                                context.getSource().getPlayer().sendMessage(Text.of("§cTarget must be a valid player"));
                            }
                            return 0;
                        })
                    )
                )
                .then(literal("add")
                    .then(argument("amount", IntegerArgumentType.integer())
                        .executes((context) -> {
                            Entity target = EntityArgumentType.getEntity(context, "target");
                            if (target instanceof ServerPlayerEntity player) {
                                Titanomach.CONFIG.getPlayerConfig(player).addProgressPoints(IntegerArgumentType.getInteger(context, "amount"));
                            } else {
                                context.getSource().getPlayer().sendMessage(Text.of("§cTarget must be a valid player"));
                            }
                            return 0;
                        })
                    )
                )
            );
    }
}
