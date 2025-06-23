package io.github.mjk134.titanomach.server;

import io.github.mjk134.titanomach.utils.RandomIdentity;
import net.minecraft.server.network.ServerPlayerEntity;

public class TitanomachPlayer {
    private String playerId;
    private Boolean hasJoined = false;
    private RandomIdentity randomIdentity;

    public TitanomachPlayer(ServerPlayerEntity player) {
        this.playerId = player.getUuid().toString();
        this.randomIdentity = RandomIdentity.getRandomIdentity(player);
    }

    public RandomIdentity getRandomIdentity() {
        return randomIdentity;
    }

    public void setSkinId(String skinId) {
        this.randomIdentity.setSkinId(skinId);
    }

}
