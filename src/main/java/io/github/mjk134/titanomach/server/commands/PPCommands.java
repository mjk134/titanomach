package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.mjk134.titanomach.Titanomach;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PPCommands {
    public static LiteralArgumentBuilder<ServerCommandSource> get() {
        return literal("pp")
                .requires(source -> source.hasPermissionLevel(4))
                .then(literal("set")
                        .then(argument("amount", IntegerArgumentType.integer())
                                .executes((context) -> {
                                    Titanomach.TITANOMACH_CONFIG.getPlayerConfig(context.getSource().getPlayer()).setProgressPoints(IntegerArgumentType.getInteger(context, "amount"));
                                    return 0;
                                })
                        )
                )
                .then(literal("add")
                        .then(argument("amount", IntegerArgumentType.integer())
                                .executes((context) -> {
                                    Titanomach.TITANOMACH_CONFIG.getPlayerConfig(context.getSource().getPlayer()).addProgressPoints(IntegerArgumentType.getInteger(context, "amount"));
                                    return 0;
                                })
                        )
                );
    }
}
