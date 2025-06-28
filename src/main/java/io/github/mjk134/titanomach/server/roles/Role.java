package io.github.mjk134.titanomach.server.roles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class Role {
    public final String name;
    public final String description;
    public final Item itemIcon;
    public final String titleFormat;

    public Role(String name, String description, Item itemIcon, String titleFormat) {
        this.name = name;
        this.description = description;
        this.itemIcon = itemIcon;
        this.titleFormat = titleFormat;
    }

    public Role(String name, String description, String itemIconID, String titleFormat) {
        this(name, description, Registries.ITEM.get(Identifier.of(itemIconID)), titleFormat);
    }

    public void onEffectTick(PlayerEntity player) {}
}

