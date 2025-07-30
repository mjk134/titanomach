package io.github.mjk134.titanomach.mixin.server;


import io.github.mjk134.titanomach.server.sacrifice.VoteManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void onServerShutDown(CallbackInfo ci) {
        // Handle sacrificing logic
        if (VoteManager.isVotePossible()) {
            UUID playerUuid = VoteManager.getSacrificalPlayerUUID();

            if (!Objects.isNull(playerUuid)) {
                TITANOMACH_CONFIG.getPlayerConfig(playerUuid.toString()).sacrificeLevel++;
            }

            ArrayList<UUID> penalized = VoteManager.getPenalizedPlayersUUID();
            penalized.forEach(id -> {
                TITANOMACH_CONFIG.getPlayerConfig(id.toString()).sacrificeLevel++;
            });

        }
        // Other logic?

        TITANOMACH_CONFIG.dump();
    }

}
