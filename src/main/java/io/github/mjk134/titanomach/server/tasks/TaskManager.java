package io.github.mjk134.titanomach.server.tasks;

import io.github.mjk134.titanomach.server.TitanomachPlayer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public class TaskManager {
    public HashMap<String, Task> tasks= new HashMap<>();
    public HashMap<String, String> playerTaskID = new HashMap<>();
    private static int tickCounter = 0;

    public void tick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter == 30) {
            for (Task task : tasks.values()) {
                task.progress = 0;
            }
            for (Map.Entry<String, String> entry : playerTaskID.entrySet()) {
                Object TaskID = entry.getValue();
                if (this.tasks.get(TaskID) instanceof CollectionTask) {
                    String PlayerID = entry.getKey();
                    this.tasks.get(TaskID).updateProgress(server.getPlayerManager().getPlayer(UUID.fromString(PlayerID)));
                }
            }

            tickCounter = 0;
        }
    }
    public Task getTask(String taskID) {
        return tasks.get(taskID);
    }
    public void addTask(Task task, String PlayerID) {
        tasks.put(task.name, task);
        playerTaskID.put(PlayerID, task.name);
    }
    public void submitTask(String taskID, ServerPlayerEntity player) {
        if (tasks.get(taskID).submitTask(player)) {
            tasks.remove(taskID);
            playerTaskID.remove(player.getName().toString());
        };
    }
}
