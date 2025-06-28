package io.github.mjk134.titanomach.server.tasks;

import net.minecraft.server.network.ServerPlayerEntity;


public class Task {
    public String name;
    private int progress;
    private int maxProgress;

    public Task(String name, int maxProgress) {
        this.name = name;
        this.maxProgress = maxProgress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void updateProgress(ServerPlayerEntity player) {
    }
    public int getMaxProgress() {return maxProgress;}
}
