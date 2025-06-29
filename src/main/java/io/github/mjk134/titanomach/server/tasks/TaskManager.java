package io.github.mjk134.titanomach.server.tasks;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public class TaskManager {
    public HashMap<String, Task> tasks = new HashMap<>();
    /// Key is the player id, assigned task id the value
    public HashMap<String, String> playerTaskIdMap = new HashMap<>();
    private static int tickCounter = 0;

    public void tick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter == 30) {
            // 0.001 ms difference
            PlayerManager playerManager = server.getPlayerManager();
            for (Task task : tasks.values()) {
                task.progress = 0;
            }
            for (Map.Entry<String, String> entry : playerTaskIdMap.entrySet()) {
                String taskId = entry.getValue();
                if (this.getTask(taskId) instanceof CollectionTask collectionTask) {
                    String playerId = entry.getKey();
                    ServerPlayerEntity player = playerManager.getPlayer(playerId);
                    // Null check required because as far as im aware if the player logs off this code will pass in null to the function
                    if (player != null) {
                        collectionTask.updateProgress(player);
                    }

                }
            }
            tickCounter = 0;
        }
    }
    public Task getTask(String taskId) {
        return tasks.get(taskId);
    }

    public void addTask(Task task, String playerId) {
        tasks.put(task.name, task);
        playerTaskIdMap.put(playerId, task.name);
        TITANOMACH_CONFIG.dump();
    }

    public Task getTaskFromPlayer(ServerPlayerEntity player) {
        String taskId = playerTaskIdMap.get(player.getUuidAsString());
        return getTask(taskId);
    }

    public void submitTask(String taskID, ServerPlayerEntity player) {
        if (tasks.get(taskID).submitTask(player)) {
            tasks.remove(taskID);
            playerTaskIdMap.remove(player.getName().toString());
        }
        TITANOMACH_CONFIG.dump();
    }
}
