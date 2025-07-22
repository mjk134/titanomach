package io.github.mjk134.titanomach.server.tasks;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public abstract class GlobalTask extends Task {
    public HashMap<String, Integer> playerContributions = new HashMap<>();

    public GlobalTask(String name, int maxProgress, int progressPointReward, String targetID) {
        super(name, maxProgress, progressPointReward, targetID);
    }

    public void addPlayerToTask(ServerPlayerEntity player) {
        playerContributions.put(player.getUuidAsString(), 0);
    }

    /// Add an amount of contribution to the specified player
    public void updatePlayerContributions(ServerPlayerEntity player, int amount) {
        String uuid = player.getUuidAsString();
        if (!playerContributions.containsKey(uuid)) {
            addPlayerToTask(player);
        }
        playerContributions.put(uuid, playerContributions.get(uuid) + amount);
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
        map.keySet().forEach((playerId) -> {
            // Get the player from the config
            TITANOMACH_CONFIG.getPlayerConfig(playerId).addProgressPoints(map.get(playerId));
        });

        TITANOMACH_CONFIG.dump();
    }
}
