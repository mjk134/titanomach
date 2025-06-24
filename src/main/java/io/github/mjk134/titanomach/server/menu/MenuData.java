package io.github.mjk134.titanomach.server.menu;

import net.minecraft.inventory.Inventory;

public class MenuData {
    private Inventory inventory;
    public final String ID;
    private String name;

    public MenuData(String ID, Inventory inventory, String name) {
        this.inventory = inventory;
        this.ID = ID;
        this.name = name;
    }
    public MenuData(String ID, Inventory inventory) {
        this.inventory = inventory;
        this.ID = ID;
        this.name = ID;
    }

    Inventory getInventory() {
        return inventory;
    }

    String getName() {
        return name;
    }
}
