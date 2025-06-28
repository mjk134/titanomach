package io.github.mjk134.titanomach.server.menu;

import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class Menu {
    private Inventory inventory;
    private String name;
    private final HashMap<Integer, MenuClickAction> slotActionMap;
    public boolean keepDefaultInventoryBehaviour = false;

    public Menu(String name) {
        this.inventory = new SimpleInventory(54);
        this.name = name;
        this.slotActionMap = new HashMap<>();
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Menu setInventory(Inventory inventory) {
        assert inventory.size() == 54;
        this.inventory = inventory;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Menu setItem(int slot, ItemStack itemStack) {
        this.inventory.setStack(slot, itemStack);
        return this;
    }

    public Menu setItem(int slot, String itemID) {
        return this.setItem(slot, new ItemStack(Registries.ITEM.get(Identifier.of(itemID))));
    }

    public Menu fillEmptyWith(ItemStack itemStack) {
        for (int i = 0; i < this.inventory.size(); ++i) {
            if (this.inventory.getStack(i).isEmpty()) {
                this.inventory.setStack(i, itemStack);
            }
        }
        return this;
    }

    public Menu fillEmptyWith(String itemID) {
        return this.fillEmptyWith(new ItemStack(Registries.ITEM.get(Identifier.of(itemID))));
    }

    public Menu fillEmptyWithGlass() {
        ItemStack fillerGlass = new ItemStack(Registries.ITEM.get(Identifier.of("minecraft:black_stained_glass_pane")));
        fillerGlass.set(DataComponentTypes.TOOLTIP_DISPLAY, new TooltipDisplayComponent(true, ReferenceSortedSets.emptySet()));
        this.fillEmptyWith(fillerGlass);
        return this;
    }

    public Menu setClickableItem(int slot, ItemStack itemStack, MenuClickAction action) {
        this.setItem(slot, itemStack);
        this.setAction(slot, action);
        return this;
    }

    public Menu setClickableItem(int slot, String itemID, MenuClickAction action) {
        return this.setClickableItem(slot, new ItemStack(Registries.ITEM.get(Identifier.of(itemID))), action);
    }

    public void setAction(int slot, MenuClickAction action) {
        slotActionMap.put(slot, action);
    }

    public MenuClickAction getAction(int slot) {
        return slotActionMap.get(slot);
    }

    public void displayTo(PlayerEntity player) {
        player.openHandledScreen(new MenuScreenHandlerFactory(this));
    }
}
