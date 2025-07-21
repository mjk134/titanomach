package io.github.mjk134.titanomach.server.tasks;

import io.github.mjk134.titanomach.utils.TextUtils;
import net.minecraft.server.network.ServerPlayerEntity;

public class SlayerTask extends Task{
    public SlayerTask(String name, int maxProgress, int progressPointReward, String targetMob) {
        super(name, maxProgress, progressPointReward, targetMob);
    }

    @Override
    public String getTargetDisplayName() {
        return TextUtils.entityIDtoName(targetID);
    }
}
