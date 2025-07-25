package io.github.mjk134.titanomach.server.tasks;

import io.github.mjk134.titanomach.Titanomach;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

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
    public void updatePlayerContribution(ServerPlayerEntity player, int amount) {
        String uuid = player.getUuidAsString();
        if (!playerContributions.containsKey(uuid)) {
            addPlayerToTask(player);
        }
        playerContributions.put(uuid, playerContributions.get(uuid) + amount);
    }

    public HashMap<String, Integer> getPlayerContributionsAsProgressPoints() {
        HashMap<String, Integer> map = new HashMap<>();
        for (String key : playerContributions.keySet()) {
            map.put(key, getPlayerContributionAsProgressPoints(key));
        }
        return map;
    }

    public int getPlayerContributionAsProgressPoints(String uuid) {
        // If player hasn't contributed any, give 0 pp
        int contribution = getPlayerContribution(uuid);
        double percentageContribution = (double) contribution / maxProgress;
        // Give minimum points
        return (int) Math.floor(percentageContribution * progressPointReward);
    }

    public int getPlayerContribution(String uuid) {
        return playerContributions.getOrDefault(uuid, 0);
    }

    public void taskComplete() {
        // Get list of players with this task Id and assign their respective PPs
        HashMap<String, Integer> map = getPlayerContributionsAsProgressPoints();
        map.keySet().forEach((playerId) -> {
            // Get the player from the config
            TITANOMACH_CONFIG.getPlayerConfig(playerId).addProgressPoints(map.get(playerId));
        });

        // Loop through each online player and send the completion message
        for (ServerPlayerEntity player : Titanomach.SERVER_INSTANCE.getPlayerManager().getPlayerList()) {
            this.sendMessage(player);
        }

        TITANOMACH_CONFIG.dump();
    }

    @Override
    public void sendMessage(ServerPlayerEntity player) {
        String uuid = player.getUuidAsString();
        player.sendMessage(Text.of("§7§l────────────────────────"));
        player.sendMessage(Text.of("§d§lGLOBAL§r§e task completed!"));
        player.sendMessage(Text.of(getFormattedName() + " §r§a§l✓"));
        player.sendMessage(Text.of("§7You contributed " + getPlayerContribution(uuid) + " " + getTargetDisplayName() + "§7 and earned §e" + getPlayerContributionAsProgressPoints(uuid) + " §aPP"));
        player.sendMessage(Text.of("§7§l────────────────────────"));
    }
}
