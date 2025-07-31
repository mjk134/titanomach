package io.github.mjk134.titanomach.server;

import io.github.mjk134.titanomach.Titanomach;
import io.github.mjk134.titanomach.server.roles.Role;
import io.github.mjk134.titanomach.server.roles.RoleManager;
import io.github.mjk134.titanomach.utils.Identity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

import static io.github.mjk134.titanomach.Titanomach.CONFIG;


public class TitanomachPlayer {
    private String playerId;
    private Boolean hasJoined = false;
    private Identity identity;
    private int progressPoints;
    private int notorietyLevel = 0;
    public float progressPointMultiplier = 1;
    /// In terms of number of sessions
    public int multiplierDuration;
    /// This has a max of 5. 0 -> 5 represent the severity of bad effects. Also incremented when a players miss a vote whilst
    public int sacrificeLevel = 0;

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

    public void setProgressPoints(int amount) {
        this.progressPoints = amount;
        CONFIG.dump();
    }

    public int getProgressPoints() { return this.progressPoints; }

    public void addProgressPoints(int amount) {
        Role prevRole = RoleManager.calculateRole(this.progressPoints);
        this.progressPoints += (int) (amount * this.progressPointMultiplier);
        Role nextRole = RoleManager.calculateRole(this.progressPoints);
        if (prevRole != nextRole) {
            nextRole.onRankUp(getPlayerEntity());
        }
        CONFIG.dump();
    }

    public void setHasJoined(Boolean hasJoined) {
        this.hasJoined = hasJoined;
    }

    public Boolean getHasJoined() {
        return hasJoined;
    }

    public int getNotorietyLevel() {
        return notorietyLevel;
    }

    public void incrementNotorietyLevel() {
        this.notorietyLevel++;
    }

    public void resetNotorietyLevel() {
        this.notorietyLevel = 0;
    }

    public boolean isHostile() {
        return notorietyLevel > 0;
    }

    public PlayerEntity getPlayerEntity() {
        return Titanomach.SERVER_INSTANCE.getPlayerManager().getPlayer(UUID.fromString(playerId));
    }
}
