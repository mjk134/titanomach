package io.github.mjk134.titanomach.utils;

import io.github.mjk134.titanomach.Titanomach;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class ItemBuilder {
    private ItemStack item;
    private final ArrayList<Text> lore;

    public ItemBuilder(Item item) {
        this.item = new ItemStack(item);
        this.lore = new ArrayList<>();
    }

    public ItemBuilder(String itemID) {
        this(Registries.ITEM.get(Identifier.of(itemID)));
    }

    public ItemBuilder setItem(Item item) {
        this.item.applyComponentsFrom(item.getComponents());
        return this;
    }

    public ItemBuilder setItem(String itemID) {
        return setItem(Registries.ITEM.get(Identifier.of(itemID)));
    }

    public ItemBuilder setName(String name) {
        return setName(Text.of("§r" + name));
    }

    public ItemBuilder setName(Text name) {
        this.item.set(DataComponentTypes.CUSTOM_NAME, name);
        return this;
    }

    public ItemBuilder setEnchanted(boolean isEnchanted) {
        this.item.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, isEnchanted);
        return this;
    }

    public ItemBuilder removeAdditionalTooltips() {
        this.item.remove(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        return this;
    }

    public ItemBuilder hideTooltip() {
        this.item.set(DataComponentTypes.TOOLTIP_DISPLAY, new TooltipDisplayComponent(true, ReferenceSortedSets.emptySet()));
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        return addLoreLine(Text.of("§r" + line));
    }

    public ItemBuilder addLoreLine(Text line) {
        this.lore.add(line);
        return this;
    }

    public ItemBuilder addLoreMultiline(String lines) {
        for (String line : lines.split("\n")) {
            addLoreLine(line);
        };
        return this;
    }

    public ItemBuilder setQuantity(int quantity) {
        this.item.setCount(quantity);
        return this;
    }

    public ItemStack create() {
        this.item.set(DataComponentTypes.LORE, new LoreComponent(lore));
        return this.item;
    }
}
