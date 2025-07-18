package io.github.mjk134.titanomach.server.tasks;

import net.minecraft.server.network.ServerPlayerEntity;


public class Task {
    public String name;
    public int progress;
    public int maxProgress;
    public int progressPointReward;

    public Task(String name, int maxProgress, int progressPointReward) {
        this.name = name;
        this.maxProgress = maxProgress;
        this.progressPointReward = progressPointReward;
    }


    public void updateProgress(ServerPlayerEntity player) {

    }

    public boolean submitTask(ServerPlayerEntity player) {return false;}

}
