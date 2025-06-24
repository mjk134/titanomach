package io.github.mjk134.titanomach.server;

import io.github.mjk134.titanomach.server.tasks.CollectionTask;
import io.github.mjk134.titanomach.server.tasks.Task;

import io.github.mjk134.titanomach.utils.RandomIdentity;

import net.minecraft.server.network.ServerPlayerEntity;


public class TitanomachPlayer {
    private String playerId;
    private Boolean hasJoined = false;
    private RandomIdentity randomIdentity;
    private String player_name;
    public Task task;
    public String taskType;

    public TitanomachPlayer(ServerPlayerEntity player) {
        this.playerId = player.getUuid().toString();
        this.randomIdentity = RandomIdentity.getRandomIdentity(player);
        this.player_name = player.getName().toString();
        this.task = new CollectionTask("test", 64, "minecraft:dirt");
    }

    public RandomIdentity getRandomIdentity() {
        return randomIdentity;
    }

    public void setSkinId(String skinId) {
        this.randomIdentity.setSkinId(skinId);
    }


}
