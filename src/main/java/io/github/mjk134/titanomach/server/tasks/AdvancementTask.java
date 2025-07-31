package io.github.mjk134.titanomach.server.tasks;

import io.github.mjk134.titanomach.utils.TextUtils;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.UUID;

import static io.github.mjk134.titanomach.Titanomach.*;

public class AdvancementTask extends Task{
    public AdvancementTask(String name, int progressPointReward, String targetAdvancementID) {
        super(name,1, progressPointReward, targetAdvancementID);
        AdvancementManager advancementManager = SERVER_INSTANCE.getAdvancementLoader().getManager();
        AdvancementEntry advancement = Objects.requireNonNull(advancementManager.get(Identifier.of(targetAdvancementID))).getAdvancementEntry();
        onAdvancementComplete(advancement, Objects.requireNonNull(SERVER_INSTANCE.getPlayerManager().getPlayer(UUID.fromString(name.split(" ")[0]))));
    }

    public void onAdvancementComplete(AdvancementEntry advancement, ServerPlayerEntity player) {
        if(player.getAdvancementTracker().getProgress(advancement).isDone()) {
            if (advancement.id().toString().equals(targetID)) {
                progress = 1;
                CONFIG.dump();
            }
        }
    }

    @Override
    public SubmitStatus submitTask(ServerPlayerEntity player) {
        if (canSubmit(player)) {
            return SubmitStatus.COMPLETED;
        }
        return SubmitStatus.FAIL;
    }

    @Override
    public String getTargetDisplayName() {
        return TextUtils.advancementIDtoName(targetID);
    }

    @Override
    public String getFormattedName() {
        TaskType tasktype = TaskType.get(this);
        return "§6" + TaskType.icon(tasktype) + " §l" + TaskType.presentVerb(tasktype).toUpperCase() + "§r§a " + this.getTargetDisplayName();
    }
}
