package io.github.mjk134.titanomach.server.tasks;

import io.github.mjk134.titanomach.server.TitanomachPlayer;
import io.github.mjk134.titanomach.utils.TextUtils;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public class TaskManager {
    /// Name of the task, Task
    public HashMap<String, Task> tasks = new HashMap<>();
    /// Key is the player id, assigned task id the value
    public HashMap<String, String> playerTaskIdMap = new HashMap<>();
    /// PlayerId, ServerBossBar
    private static final HashMap<String, ServerBossBar> individualBossBars = new HashMap<>();
    private static int tickCounter = 0;
    public final List<String> completedGlobalTasks = new ArrayList<>();

    public void tick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter == 30) {
            // 0.001 ms difference
            PlayerManager playerManager = server.getPlayerManager();
            for (ServerPlayerEntity player : playerManager.getPlayerList()) {
                String playerId = player.getUuidAsString();
                Task task = getTaskFromPlayer(player);
                ServerBossBar bossBar = individualBossBars.get(playerId);

                // Check a task exists for the player, if not then cleanup here
                if (task == null) {
                    // remove boss bar
                    individualBossBars.remove(playerId);
                    // if boss bar exists, remove the player so its garbage collected
                    if (bossBar != null) bossBar.removePlayer(player);
                    continue;
                }

                if (task.canSubmit(player)) {
                    task.sendMessage(player);
                }

                // Check if there is a progress bar for this player
                if (bossBar == null) {
                    // If it's not in the map put it in
                    String barText = task.getFormattedName();
                    individualBossBars.put(playerId, new ServerBossBar(Text.literal(barText), BossBar.Color.GREEN, BossBar.Style.NOTCHED_10));
                    bossBar = individualBossBars.get(playerId);
                }

                // If player is not in the boss bar, add it (fixes boss bar not showing on reconnect)
                if (!bossBar.getPlayers().contains(player)) {
                    bossBar.addPlayer(player);
                };

                float percentageProgress = task.getPercentageProgress();
                if (percentageProgress > 1) {
                    bossBar.setPercent(1);
                }
                else {
                    bossBar.setPercent(percentageProgress);
                }
            }
            tickCounter = 0;
        }
    }
    public Task getTask(String taskName) {
        return tasks.get(taskName);
    }

    @Deprecated
    public void updateBossBar(ServerPlayerEntity player) {
        String playerId = player.getUuidAsString();
        Task task = this.getTaskFromPlayer(player);
        ServerBossBar bossBar = individualBossBars.get(playerId);
        if (bossBar != null) {
            bossBar.setPercent((float) task.progress / task.maxProgress);
        }
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

    public Task getTaskFromPlayer(TitanomachPlayer player) {
        String taskId = playerTaskIdMap.get(player.getPlayerId());
        return getTask(taskId);
    }

    public SubmitStatus submitTask(String taskID, ServerPlayerEntity player) {
        Task task = tasks.get(taskID);
        SubmitStatus status = task.submitTask(player);
        if (status == SubmitStatus.COMPLETED) {
            TITANOMACH_CONFIG.getPlayerConfig(player).addProgressPoints(task.progressPointReward);
            tasks.remove(taskID);
            playerTaskIdMap.remove(player.getUuidAsString());

            if (task instanceof GlobalTask) {
                completedGlobalTasks.add(taskID);
            }
        }
        TITANOMACH_CONFIG.dump();
        return status;
    }

    public void resetGlobalTasks() {
        completedGlobalTasks.clear();
    }
}
