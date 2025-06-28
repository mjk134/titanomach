package io.github.mjk134.titanomach.server;

import io.github.mjk134.titanomach.server.roles.RoleManager;
import io.github.mjk134.titanomach.server.tasks.CollectionTask;
import io.github.mjk134.titanomach.server.tasks.Task;

import io.github.mjk134.titanomach.utils.RandomIdentity;

import net.minecraft.server.network.ServerPlayerEntity;


public class TitanomachPlayer {
    private String playerId;
    private Boolean hasJoined = false;
    private RandomIdentity randomIdentity;
    private String roleName; // a string reference to role type
    public Task task;
    public String taskType;

    public TitanomachPlayer(ServerPlayerEntity player) {
        this.playerId = player.getUuid().toString();
        this.randomIdentity = RandomIdentity.getRandomIdentity(player);
        this.roleName = RoleManager.DEFAULT_ROLE_NAME;
        this.task = new CollectionTask("test", 64, "minecraft:dirt");
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

    public void setRoleName(String roleName) {this.roleName = roleName;}

    public String getRoleName() {return this.roleName;}

}
