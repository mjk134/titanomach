package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.context.CommandContext;
import io.github.mjk134.titanomach.server.sacrifice.VoteManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Objects;
import java.util.UUID;

import static io.github.mjk134.titanomach.Titanomach.CONFIG;

public class DoSacrificeCommand {

    public static int run(CommandContext<ServerCommandSource> context) {
        VoteManager.reduceSacrificeLevels();
        if (VoteManager.isVotePossible()) {
            UUID playerUuid = VoteManager.getSacrificalPlayerUUID();
            if (!Objects.isNull(playerUuid)) {
                CONFIG.getPlayerConfig(playerUuid.toString()).sacrificeLevel++;
            }
//            ArrayList<UUID> penalized = VoteManager.getPenalizedPlayersUUID();
//            penalized.forEach(id -> {
//                CONFIG.getPlayerConfig(id.toString()).sacrificeLevel++;
//            });
        }
        // Other logic?
        CONFIG.dump();

        return 0;
    }


}
