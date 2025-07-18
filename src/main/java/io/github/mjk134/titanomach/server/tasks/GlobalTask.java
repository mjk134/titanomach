package io.github.mjk134.titanomach.server.tasks;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public abstract class GlobalTask extends Task {
    public HashMap<String, Integer> playerContributions = new HashMap<>();

    public GlobalTask(String name, int maxProgress, int progressPointReward) {
        super(name, maxProgress, progressPointReward);
    }

    public void addPlayerToTask(ServerPlayerEntity player) {
        playerContributions.put(player.getUuidAsString(), 0);
    }

    public void updatePlayerContributions(ServerPlayerEntity player, int progress) {
        playerContributions.put(player.getUuidAsString(), progress);
    }

    public HashMap<String, Integer> getPlayerContributionsAsProgressPoints() {
        HashMap<String, Integer> map = new HashMap<>();
        for (String key : playerContributions.keySet()) {
            int contribution = playerContributions.get(key);
            double percentageContribution = (double) contribution / maxProgress;
            // Give minimum points
            int playerPpReward = (int) Math.floor(percentageContribution * progressPointReward);
            map.put(key, playerPpReward);
        }
        return map;
    }

    public void taskComplete() {
        // Get list of players with this task Id and assign their respective PPs
        HashMap<String, Integer> map = getPlayerContributionsAsProgressPoints();
        TITANOMACH_CONFIG.getTaskManager().playerTaskIdMap.keySet().forEach((playerId) -> {
            // Get the player from the config
            TITANOMACH_CONFIG.getPlayerConfig(playerId).addProgressPoints(map.get(playerId));
            TITANOMACH_CONFIG.getTaskManager().playerTaskIdMap.remove(playerId);
        });
        // Remove from task pool
        TITANOMACH_CONFIG.getTaskManager().tasks.remove(this.name);
        TITANOMACH_CONFIG.dump();
    }

    public abstract boolean updateProgress(ServerPlayerEntity player, ItemStack itemStack);
}
