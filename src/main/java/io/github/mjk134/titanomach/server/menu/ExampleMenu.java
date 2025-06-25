package io.github.mjk134.titanomach.server.menu;

import io.github.mjk134.titanomach.Titanomach;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ExampleMenu extends Menu {
    public ExampleMenu(int additionalContext) {
        // initialize the menu with title
        super("Example Menu");

        // example action - maybe put this in a separate class
        MenuClickAction exampleAction = new MenuClickAction() {
            public void onClick(PlayerEntity player, int slot, ItemStack itemStack) {
                Titanomach.ModLogger.info("I was clicked!");
            }
        };

        this.setItem(0, "minecraft:red_wool") // add a non clickable item
                .setItem(1, "minecraft:black_wool")
                .setClickableItem(30, "minecraft:nether_star", exampleAction) // add a clickable item, binding it to the example action
                .setItem(53, new ItemStack(Registries.ITEM.get(Identifier.of("minecraft:stick")), additionalContext)); // add an item that depends on the additional context passed in, e.g. a player
    }
}