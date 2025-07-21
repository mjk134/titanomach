package io.github.mjk134.titanomach.server.tasks;

import net.minecraft.server.network.ServerPlayerEntity;


public class Task {
    public String name;
    public int progress;
    public int maxProgress;
    public int progressPointReward;
    public String targetID;

    public Task(String name, int maxProgress, int progressPointReward, String targetID) {
        this.name = name;
        this.maxProgress = maxProgress;
        this.progressPointReward = progressPointReward;
        this.targetID = targetID;
    }

    public void updateProgress(ServerPlayerEntity player) {

    }

    public boolean submitTask(ServerPlayerEntity player) {return false;}

    /// Get name of the task target that can be displayed
    public String getTargetDisplayName() {
        return targetID;
    }

    public float getPercentageProgress() {
        return (float) progress / maxProgress;
    }
}
