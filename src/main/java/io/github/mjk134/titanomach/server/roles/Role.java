package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.server.tasks.CollectionTask;
import io.github.mjk134.titanomach.server.tasks.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public abstract class Role {
    public final String name;
    public final String description;
    public final Item itemIcon;
    public final String titleFormat;
    public final int pointRequirement;
    private final List<ItemStack> rankUpRewards;

    public Role(String name, String description, String itemIconID, String titleFormat, int pointRequirement) {
        this.name = name;
        this.description = description;
        this.itemIcon = Registries.ITEM.get(Identifier.of(itemIconID));
        this.titleFormat = titleFormat;
        this.pointRequirement = pointRequirement;
        this.rankUpRewards = new ArrayList<>();
    }

    /// Is called periodically to apply effects from roles to a player.
    /// Previous role effects are also applied cumulatively, starting from the Peasant role
    public void onEffectTick(PlayerEntity player) {}

    /// Called when this role is first reached
    public void onRankUp(PlayerEntity player) {
        for (ItemStack rankUpReward : this.rankUpRewards) {
            player.giveItemStack(rankUpReward);
        }
    }

    public void addRankUpReward(String itemID, int amount) {
        this.rankUpRewards.add(new ItemStack(Registries.ITEM.get(Identifier.of(itemID)), amount));
    }

    public void addRankUpReward(ItemStack rankUpReward) {
        this.rankUpRewards.add(rankUpReward);
    }

    public List<ItemStack> getRankUpRewards() {
        return this.rankUpRewards;
    }

    public Task[] getGlobalTasks() {
        return new Task[]{};
    }
    public Task[] getPlayerTasks() { return new Task[]{}; }
}

