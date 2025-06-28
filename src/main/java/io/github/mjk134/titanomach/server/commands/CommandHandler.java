package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandHandler {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal("titanomach")
                .requires(source -> source.hasPermissionLevel(4))
                    .then(literal("helloworld").executes(HelloWorldCommand::run))
                    .then(literal("enable").executes(EnableCommand::run))
                    .then(literal("disable").executes(DisableCommand::run))
                    .then(literal("start").executes(StartCommand::run))
                    .then(literal("addTask").executes(AddTaskCommand::run))
                    .then(literal("addskin")
                            .then(argument("skinId", StringArgumentType.string())
                                    .then(argument("texture", StringArgumentType.string())
                                            .then(argument("signature", StringArgumentType.string())
                                                    .executes(AddSkinCommand::run)))
                            )

                    )
        );

        dispatcher.register(literal("setskin")
                .then(argument("skinId", StringArgumentType.string())
                        .executes(SetSkinCommand::run)));

        dispatcher.register(
                literal("testmenu")
                        .requires(source -> source.hasPermissionLevel(4))
                        .executes(MenuTestCommand::run)
        );

        dispatcher.register(
                literal("roles")
                        .executes(RoleMenuCommand::run)
        );
    }

}
