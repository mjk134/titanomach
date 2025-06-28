package io.github.mjk134.titanomach.server.menu;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ExampleMenu extends Menu {
    public int count = 1;
    // a player context is passed so that the menu can be personalised to that player
    // this is not necessary if the menu items do not depend on the player
    public ExampleMenu(PlayerEntity playerContext) {
        // initialize the menu with title
        super("Example Menu");

        // example action as a lambda - can also be in a subclass that implements MenuClickAction
        MenuClickAction incrementAction = (player, slot, menuContext) -> {
          this.count++;
          if (this.count > 64) this.count = 1;
          this.getInventory().getStack(slot).setCount(this.count);
        };

        MenuClickAction nextMenuAction = (player, slot, menuContext) -> {
            Menu menu = new ExampleMenu2();
            menu.displayTo(player);
        };

        ItemStack nextArrow = new ItemStack(Registries.ITEM.get(Identifier.of("minecraft:arrow")));
        nextArrow.set(DataComponentTypes.CUSTOM_NAME, Text.of("Next"));

        ItemStack emeraldIcon = new ItemStack(Registries.ITEM.get(Identifier.of("minecraft:emerald")));
        emeraldIcon.set(DataComponentTypes.CUSTOM_NAME, Text.of("Click me!"));

        ItemStack headItem = new ItemStack(Registries.ITEM.get(Identifier.of("minecraft:skeleton_skull")));
        headItem.set(DataComponentTypes.CUSTOM_NAME, Text.of("Your UUID: " + playerContext.getUuid().toString()));

        this.setItem(4, "minecraft:red_wool") // add a non clickable item
                .setClickableItem(8, nextArrow, nextMenuAction)
                .setClickableItem(22, emeraldIcon, incrementAction)
                .setItem(53, headItem) // add an item that depends on the additional context passed in, e.g. a player
                .fillEmptyWithGlass();
    }

    // this menu is nested in the first one so that this example is all in one file
    class ExampleMenu2 extends Menu {
        public ExampleMenu2() {
            super("Example Menu 2");

            MenuClickAction closeAction = (player, slot, menuContext) -> {
                ((ServerPlayerEntity)player).closeHandledScreen();
            };

            ItemStack closeIcon = new ItemStack(Registries.ITEM.get(Identifier.of("minecraft:barrier")));
            closeIcon.set(DataComponentTypes.CUSTOM_NAME, Text.of("Close"));

            this.setItem(31, "minecraft:potato")
                    .setClickableItem(0, closeIcon, closeAction);
        }
    }
}