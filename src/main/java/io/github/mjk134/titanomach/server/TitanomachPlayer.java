package io.github.mjk134.titanomach.server;

import io.github.mjk134.titanomach.utils.RandomIdentity;

import net.minecraft.server.network.ServerPlayerEntity;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;


public class TitanomachPlayer {
    private String playerId;
    private Boolean hasJoined = false;
    private RandomIdentity randomIdentity;
    private int progressPoints;

    public TitanomachPlayer(ServerPlayerEntity player) {
        this.playerId = player.getUuid().toString();
        this.randomIdentity = RandomIdentity.getRandomIdentity(player);
        this.progressPoints = 0;
    }

    public RandomIdentity getRandomIdentity() {
        return randomIdentity;
    }

    public void setSkinId(String skinId) {
        this.randomIdentity.setSkinId(skinId);
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setProgressPoints(int progressPoints) {
        this.progressPoints = progressPoints;
        TITANOMACH_CONFIG.dump();
    }

    public int getProgressPoints() {return this.progressPoints;}

    public void addProgressPoints(int progressPoints) {
        this.progressPoints += progressPoints;
        TITANOMACH_CONFIG.dump();
    }

}
