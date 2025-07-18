package io.github.mjk134.titanomach.server.tasks;

import net.minecraft.server.network.ServerPlayerEntity;

public class SlayerTask extends Task{
    private final String targetMob;
    public SlayerTask(String name, int maxProgress, int progressPointReward, String targetMob) {
        super(name, maxProgress, progressPointReward);
        this.targetMob = targetMob;
    }

}
