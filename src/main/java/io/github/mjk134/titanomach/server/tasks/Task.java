package io.github.mjk134.titanomach.server.tasks;

import io.github.mjk134.titanomach.utils.TextUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;


public class Task {
    public String name;
    public int progress;
    public int maxProgress;
    public int progressPointReward;
    public String targetID;
    private boolean messageSent = false;

    public Task(String name, int maxProgress, int progressPointReward, String targetID) {
        this.name = name;
        this.maxProgress = maxProgress;
        this.progressPointReward = progressPointReward;
        this.targetID = targetID;
    }


    public SubmitStatus submitTask(ServerPlayerEntity player) {
        if (canSubmit(player)) {
            return SubmitStatus.COMPLETED;
        }
        return SubmitStatus.FAIL;
    }

    public boolean canSubmit(ServerPlayerEntity player) {return progress >= maxProgress;}

    public void sendMessage(ServerPlayerEntity player) {
        if (!messageSent) {
            player.sendMessage(Text.of(TextUtils.SECTION_BREAK));
            player.sendMessage(Text.of("§eTask completed!"));
            player.sendMessage(Text.of(getFormattedName() + " §r§a§l✓"));
            player.sendMessage(Text.literal("§9§l[CLICK HERE] §r§eto submit").setStyle(Style.EMPTY.withClickEvent(new ClickEvent.RunCommand("/tasks")).withHoverEvent(new HoverEvent.ShowText(Text.of("§eClick to open the §9/tasks §r§emenu")))));
            player.sendMessage(Text.of(TextUtils.SECTION_BREAK));
            messageSent = true;
        }
    }

    /// Get name of the task target that can be displayed
    public String getTargetDisplayName() {
        return targetID;
    }

    public String getFormattedName() {
        TaskType tasktype = TaskType.get(this);
        return "§6" + TaskType.icon(tasktype) + " §l" + TaskType.presentVerb(tasktype).toUpperCase() + "§r§f " + this.maxProgress + " " + this.getTargetDisplayName();
    }

    public float getPercentageProgress() {
        return (float) progress / maxProgress;
    }

    public float getOptimisticPercentageProgress(ServerPlayerEntity player) {
        return getPercentageProgress();
    }
}
