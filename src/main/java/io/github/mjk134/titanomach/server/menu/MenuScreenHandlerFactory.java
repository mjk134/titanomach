package io.github.mjk134.titanomach.server.menu;

import net.fabricmc.fabric.api.screenhandler.v1.FabricScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public class MenuScreenHandlerFactory implements NamedScreenHandlerFactory, FabricScreenHandlerFactory {
    private final Menu menu;

    public MenuScreenHandlerFactory(Menu menu) {
        this.menu = menu;
    }

    public Text getDisplayName() {
        return Text.of(menu.getName());
    }

    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new MenuScreenHandler(syncId, playerInventory, menu);
    }

    public boolean shouldCloseCurrentScreen() {
        return false;
    }
}
