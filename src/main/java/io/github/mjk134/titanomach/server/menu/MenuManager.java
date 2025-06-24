package io.github.mjk134.titanomach.server.menu;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class MenuManager {
    private static final HashMap<String, MenuData> menuDataMap = new HashMap<>();

    public static void createMenus() {
        Inventory inventory = new SimpleInventory(9*6);
        inventory.setStack(0, new ItemStack(Registries.ITEM.get(Identifier.of("minecraft:stick"))));
        addMenu(
                new MenuData("Test Menu Item", inventory)
        );
    }

    public static void addMenu(MenuData menuData) {
        menuDataMap.put(menuData.ID, menuData);
    }
    public static MenuData getMenu(String menuID) {
        return menuDataMap.get(menuID);
    }
}
