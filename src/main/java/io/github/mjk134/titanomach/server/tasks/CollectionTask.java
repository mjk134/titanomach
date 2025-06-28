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
import net.minecraft.util.Identifier;

public class CollectionTask extends Task{
    private String targetItem;
    private NamedScreenHandlerFactory screen;

    public CollectionTask(String name, int maxProgress, String targetItem) {
        super(name, maxProgress);
        this.targetItem = targetItem;
    }

    @Override
    public void updateProgress(ServerPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
    }

}
