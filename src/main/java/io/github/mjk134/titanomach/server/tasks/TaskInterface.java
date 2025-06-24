package io.github.mjk134.titanomach.server.tasks;

import net.minecraft.server.network.ServerPlayerEntity;

public interface TaskInterface {
    int getProgress();
    void setProgress(int progress);
    void OpenTaskMenu(ServerPlayerEntity player);
}