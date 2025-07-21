package io.github.mjk134.titanomach.server.tasks;

import io.github.mjk134.titanomach.utils.TextUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public class SlayerTask extends Task{
    public SlayerTask(String name, int maxProgress, int progressPointReward, String targetMob) {
        super(name, maxProgress, progressPointReward, targetMob);
    }

    public void onEntityKill(ServerPlayerEntity player, LivingEntity other) {
        String entityID = "minecraft:" + other.getType().getUntranslatedName();
        if (entityID.equals(targetID)) {
            progress++;
            TITANOMACH_CONFIG.dump();
        }
    }

    @Override
    public boolean submitTask(ServerPlayerEntity player) {
        return this.progress >= this.maxProgress;
    }

    @Override
    public String getTargetDisplayName() {
        return TextUtils.entityIDtoName(targetID);
    }
}
