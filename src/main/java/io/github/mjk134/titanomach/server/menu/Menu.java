package io.github.mjk134.titanomach.server.menu;

import net.minecraft.entity.player.PlayerEntity;

public class Menu {
    private MenuScreenHandlerFactory screenHandlerFactory;

    public Menu(String menuID) {
        MenuData menuData = MenuManager.getMenu(menuID);
        this.screenHandlerFactory = new MenuScreenHandlerFactory(menuData.getName(), menuData.getInventory());
    }

    public void display(PlayerEntity player) {
        player.openHandledScreen(screenHandlerFactory);
    }
}
