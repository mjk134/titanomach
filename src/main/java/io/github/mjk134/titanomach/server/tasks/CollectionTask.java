package io.github.mjk134.titanomach.server.tasks;

import io.github.mjk134.titanomach.utils.TextUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public class CollectionTask extends Task{
    private NamedScreenHandlerFactory screen;

    public CollectionTask(String name, int maxProgress, int progressPointReward, String targetItem) {
        super(name, maxProgress, progressPointReward, targetItem);
    }

    @Override
    // returns whether the task can be submitted
    public SubmitStatus submitTask(ServerPlayerEntity player) {
        SubmitStatus status = SubmitStatus.FAIL;
        PlayerInventory inventory = player.getInventory();
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
        TITANOMACH_CONFIG.dump();
        return status;
    }

    @Override
    public boolean canSubmit(ServerPlayerEntity player) {
        return progress + getInventoryCount(player) >= this.maxProgress;
    }

    public int getInventoryCount(ServerPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        int inventoryCount = 0;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.getItem().toString().equals(targetID)) {
                inventoryCount += itemStack.getCount();
            }
        }
        return  inventoryCount;
    }

    @Override
    public String getTargetDisplayName() {
        return TextUtils.itemIDtoName(targetID);
    }

    @Override
    public float getOptimisticPercentageProgress(ServerPlayerEntity player) {
        int numInInventory = this.getInventoryCount(player);
        return (float) (numInInventory + this.progress) / this.maxProgress;
    }
}
