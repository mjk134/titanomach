package io.github.mjk134.titanomach.server.tasks;

import io.github.mjk134.titanomach.utils.TextUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import static io.github.mjk134.titanomach.Titanomach.CONFIG;

public class GlobalSlayerTask extends GlobalTask {

    public GlobalSlayerTask(String name, int maxProgress, int progressPointReward, String targetMob) {
        super(name, maxProgress, progressPointReward, targetMob);
    }

    public void onEntityKill(ServerPlayerEntity player, LivingEntity other) {
        String entityID = "minecraft:" + other.getType().getUntranslatedName();
        if (entityID.equals(targetID)) {
            updatePlayerContribution(player, 1);
            progress++;

            if (canSubmit(player)) {
                CONFIG.getTaskManager().submitTask(name, player);
            }

            CONFIG.dump();
        }
    }

    @Override
    public String getTargetDisplayName() {
        return TextUtils.entityIDtoName(targetID);
    }
}
