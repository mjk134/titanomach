package io.github.mjk134.titanomach.server.tasks;

import io.github.mjk134.titanomach.utils.TextUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;

import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

public class CollectionTask extends Task{
    private NamedScreenHandlerFactory screen;
    private int bankedProgress;
    public int inventoryCount;

    public CollectionTask(String name, int maxProgress, int progressPointReward, String targetItem) {
        super(name, maxProgress, progressPointReward, targetItem);
        int bankedProgress = 0;
    }

    @Override
    public void updateProgress(ServerPlayerEntity player) {
        inventoryCount = 0;
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.getItem().toString().equals(targetID)) {
                inventoryCount += itemStack.getCount();
            }
        }
        this.progress = this.bankedProgress + inventoryCount;
//        int percentProgress = ((int) ((((float) this.progress) / this.maxProgress) * 100));
//        if (percentProgress >= 100) {
//            percentProgress = 100;
//        }
//        if (percentProgress == 100) {
//            player.sendMessage(Text.literal(String.valueOf(percentProgress)).withColor(Formatting.GREEN.getColorValue().intValue()), true);
//        }
//        else {
//            player.sendMessage(Text.literal(String.valueOf(percentProgress)), true);
//        }
        TITANOMACH_CONFIG.dump();
    }

    @Override
    // returns whether the task can be submitted
    public boolean submitTask(ServerPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        if (this.progress >= this.maxProgress) {
            this.maxProgress -= this.bankedProgress;
            for (int i = 0; i < inventory.size(); ++i) {
                ItemStack itemStack = inventory.getStack(i);
                if (itemStack.getItem().toString().equals(targetID)) {
                    if (maxProgress - itemStack.getCount()<= 0) {
                        this.progress = 0;
                        inventory.setStack(i, new ItemStack(itemStack.getItem()).copyWithCount(itemStack.getCount() - maxProgress));
                        maxProgress = 0;
                        return true;
                    }
                    this.maxProgress -= itemStack.getCount();
                    inventory.setStack(i, ItemStack.EMPTY);
                }
            }
        }
        else {
            for (int i = 0; i < inventory.size(); ++i) {
                ItemStack itemStack = inventory.getStack(i);
                if (itemStack.getItem().toString().equals(targetID)) {
                    this.bankedProgress += itemStack.getCount();
                    inventory.setStack(i, ItemStack.EMPTY);
                }
            }
        }
        return false;
    }

    @Override
    public String getTargetDisplayName() {
        return TextUtils.itemIDtoName(targetID);
    }
}
