package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.server.tasks.CollectionTask;
import io.github.mjk134.titanomach.server.tasks.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public abstract class Role {
    public final String name;
    public final String description;
    public final Item itemIcon;
    public final String titleFormat;
    public final int pointRequirement;

    public Role(String name, String description, Item itemIcon, String titleFormat, int pointRequirement) {
        this.name = name;
        this.description = description;
        this.itemIcon = itemIcon;
        this.titleFormat = titleFormat;
        this.pointRequirement = pointRequirement;
    }

    public Role(String name, String description, String itemIconID, String titleFormat, int pointRequirement) {
        this(name, description, Registries.ITEM.get(Identifier.of(itemIconID)), titleFormat, pointRequirement);
    }

    public void onEffectTick(PlayerEntity player) {}

    public Task[] getGlobalTasks() {
        return new Task[]{};
    }
    public Task[] getPlayerTasks() { return new Task[]{}; }
}

