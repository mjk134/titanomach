package io.github.mjk134.titanomach.server.tasks;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import static io.github.mjk134.titanomach.Titanomach.MOD_LOGGER;

public class CollectionTask extends Task{
    private final String targetItem;
    private NamedScreenHandlerFactory screen;

    public CollectionTask(String name, int maxProgress, String targetItem) {
        super(name, maxProgress);
        this.targetItem = targetItem;
    }

    @Override
    public void updateProgress(ServerPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.getItem().toString().equals(targetItem)) {
                this.progress += itemStack.getCount();
            }
        }
        int percentProgress = ((int) ((((float) this.progress) / this.maxProgress) * 100));
        if (percentProgress >= 100) {
            percentProgress = 100;
        }
        if (percentProgress == 100) {
            player.sendMessage(Text.literal(String.valueOf(percentProgress)).withColor(Formatting.GREEN.getColorValue().intValue()), true);
        }
        else {
            player.sendMessage(Text.literal(String.valueOf(percentProgress)), true);
        }
    }

    @Override
    // returns whether the task can be submitted
    public boolean submitTask(ServerPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        if (this.progress >= this.maxProgress) {
            for (int i = 0; i < inventory.size(); ++i) {
                ItemStack itemStack = inventory.getStack(i);
                if (itemStack.getItem().toString().equals(targetItem)) {
                    if (maxProgress - itemStack.getCount() <= 0) {
                        this.progress = 0;
                        inventory.setStack(i, new ItemStack(itemStack.getItem()).copyWithCount(itemStack.getCount() - maxProgress));
                        maxProgress = 0;
                        return true;
                    }
                    this.progress -= itemStack.getCount();
                    this.maxProgress -= itemStack.getCount();
                    inventory.setStack(i, ItemStack.EMPTY);
                }
            }
        }
        return false;
    }
}
