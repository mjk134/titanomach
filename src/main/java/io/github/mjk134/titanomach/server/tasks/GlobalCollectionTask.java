package io.github.mjk134.titanomach.server.tasks;

import io.github.mjk134.titanomach.utils.TextUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public class GlobalCollectionTask extends GlobalTask {
    public GlobalCollectionTask(String name, int maxProgress, int progressPointReward, String targetItem) {
        super(name, maxProgress, progressPointReward, targetItem);
    }

    @Override
    public SubmitStatus submitTask(ServerPlayerEntity player) {
        SubmitStatus status = SubmitStatus.FAIL;
        PlayerInventory inventory = player.getInventory();
        int prevProgress = progress;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.getItem().toString().equals(targetID)) {
                if (progress + itemStack.getCount() < maxProgress) {
                    progress += itemStack.getCount();
                    inventory.setStack(i, ItemStack.EMPTY);
                    status = SubmitStatus.PARTIAL;
                }
                else  {
                    itemStack.setCount(itemStack.getCount() - (maxProgress-progress));
                    progress = maxProgress;
                    status = SubmitStatus.COMPLETED;
                }
            }
        }

        updatePlayerContribution(player, progress - prevProgress);
        TITANOMACH_CONFIG.dump();
        return status;
    }

    @Override
    public String getTargetDisplayName() {
        return TextUtils.itemIDtoName(targetID);
    }
}
