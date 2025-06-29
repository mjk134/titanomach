package io.github.mjk134.titanomach.server;

import io.github.mjk134.titanomach.utils.Identity;

import net.minecraft.server.network.ServerPlayerEntity;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;


public class TitanomachPlayer {
    private String playerId;
    private Boolean hasJoined = false;
    private Identity identity;
    private int progressPoints;

    public TitanomachPlayer(ServerPlayerEntity player) {
        this.playerId = player.getUuid().toString();
        this.identity = Identity.getRandomIdentity(player);
        this.progressPoints = 0;
    }

    public Identity getRandomIdentity() {
        return identity;
    }

    public void setSkinId(String skinId) {
        this.identity.setSkinId(skinId);
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setProgressPoints(int progressPoints) {
        this.progressPoints = progressPoints;
        TITANOMACH_CONFIG.dump();
    }

    public int getProgressPoints() { return this.progressPoints; }

    public void addProgressPoints(int progressPoints) {
        this.progressPoints += progressPoints;
        TITANOMACH_CONFIG.dump();
    }

    public void setHasJoined(Boolean hasJoined) {
        this.hasJoined = hasJoined;
    }

    public Boolean getHasJoined() {
        return hasJoined;
    }
}
