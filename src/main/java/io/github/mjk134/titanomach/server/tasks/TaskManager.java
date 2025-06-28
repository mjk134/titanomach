package io.github.mjk134.titanomach.server.tasks;

import io.github.mjk134.titanomach.server.TitanomachPlayer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    public HashMap<String, Task> tasks= new HashMap<>();
    public HashMap<String, String> playerTaskID = new HashMap<>();

    public void initialise() {
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            for (Map.Entry<String, String> entry : playerTaskID.entrySet()) {
                String PlayerID = entry.getKey();
                Object TaskID = entry.getValue();
                this.tasks.get(TaskID).updateProgress(server.getPlayerManager().getPlayer(PlayerID));
            }
        });
    }
    public Task getTask(String taskID) {
        return tasks.get(taskID);
    }
    public void addTask(Task task, String PlayerID) {
        tasks.put(task.name, task);
        playerTaskID.put(PlayerID, task.name);
    }
}
