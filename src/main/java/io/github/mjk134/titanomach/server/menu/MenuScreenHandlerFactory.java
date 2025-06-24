package io.github.mjk134.titanomach.server.menu;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public class MenuScreenHandlerFactory implements NamedScreenHandlerFactory {
    private final String name;
    private final Inventory inventory;

    public MenuScreenHandlerFactory(String name, Inventory inventory) {
        this.name = name;
        this.inventory = inventory;
    }

    public Text getDisplayName() {
        return Text.of(name);
    }

    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory, inventory);
    }
}
