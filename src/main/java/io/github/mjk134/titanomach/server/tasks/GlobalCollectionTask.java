package io.github.mjk134.titanomach.server.tasks;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public class GlobalCollectionTask extends GlobalTask {
    private final String targetItem;

    public GlobalCollectionTask(String name, int maxProgress, int progressPointReward, String targetItem) {
        super(name, maxProgress, progressPointReward, targetItem);
        this.targetItem = targetItem;
    }

    /**
     * This method is called on the submission of task data.
     * If true remove the item stack in the display slot
     * If false don't do anything and move it back to where it was after the menu is closed
     */
    @Override
    public boolean updateProgress(ServerPlayerEntity player, ItemStack itemStack) {
        // First, check if item stack is of the target item
        if (!itemStack.getItem().toString().equals(targetItem)) {
            return false;
        }
        int itemCount = itemStack.getCount();
        // Since we know it's the target item, update player progress and the task progress
        this.progress += itemCount;

        if (this.progress >= maxProgress) {
            // run taskComplete and remove it off the player's tasks
            this.taskComplete();
            return true;
        }

        this.updatePlayerContributions(player, itemCount);

        // Dump config, tick checker should automatically update progress via bb
        TITANOMACH_CONFIG.dump();
        return true;
    }
}
