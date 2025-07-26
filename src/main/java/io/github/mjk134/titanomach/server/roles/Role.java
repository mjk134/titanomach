package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.Titanomach;
import io.github.mjk134.titanomach.server.tasks.GlobalTask;
import io.github.mjk134.titanomach.server.tasks.TaskInfo;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
    private final List<StatusEffectInstance> effects;
    private final List<TaskInfo> playerTaskPool;
    private final List<GlobalTask> globalTaskPool;

    public Role(String name, String description, String itemIconID, String titleFormat, int pointRequirement) {
        this.name = name;
        this.description = description;
        this.itemIcon = Registries.ITEM.get(Identifier.of(itemIconID));
        this.titleFormat = titleFormat;
        this.pointRequirement = pointRequirement;
        this.rankUpRewards = new ArrayList<>();
        this.effects = new ArrayList<>();
        this.playerTaskPool = new ArrayList<>();
        this.globalTaskPool = new ArrayList<>();
    }

    /// Is called periodically to apply effects from roles to a player.
    /// Previous role effects are also applied cumulatively, starting from the Peasant role
    public void onEffectTick(PlayerEntity player) {
        for (StatusEffectInstance effect : this.effects) {
            player.addStatusEffect(new StatusEffectInstance(effect));
        }
    }

    /// Called when this role is first reached
    public void onRankUp(PlayerEntity player) {
        for (ItemStack rankUpReward : this.rankUpRewards) {
            player.giveItemStack(rankUpReward);
        }

        ServerWorld world = (ServerWorld) player.getWorld();
        ItemStack fireworkItem = new ItemStack(Items.FIREWORK_ROCKET);
        fireworkItem.set(
                DataComponentTypes.FIREWORKS,
                new FireworksComponent(1, List.of(
                        new FireworkExplosionComponent(FireworkExplosionComponent.Type.LARGE_BALL, IntList.of(0xFFFFFF), IntList.of(), false, true),
                        new FireworkExplosionComponent(FireworkExplosionComponent.Type.LARGE_BALL, IntList.of(0xFF0000), IntList.of(), false, true),
                        new FireworkExplosionComponent(FireworkExplosionComponent.Type.LARGE_BALL, IntList.of(0x0000FF), IntList.of(), false, true)
                ))
        );

        FireworkRocketEntity firework = new FireworkRocketEntity(world, player.getX(), player.getY(), player.getZ(), fireworkItem);
        world.spawnEntity(firework);

        player.playSoundToPlayer(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.UI, 1.0f, 1.0f);
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

    public void addEffect(String effectID, int level) {
        this.effects.add(new StatusEffectInstance(
                Registries.STATUS_EFFECT.getEntry(Identifier.of(effectID)).get(),
                -1, // infinite duration
                level - 1,
                false,
                false,
                false
        ));
    }

    public List<StatusEffectInstance> getEffects() {
        return this.effects;
    }

    public void addPlayerTask(TaskInfo task) {
        this.playerTaskPool.add(task);
    }

    public void addGlobalTask(GlobalTask task) {
        this.globalTaskPool.add(task);
        Titanomach.TITANOMACH_CONFIG.getTaskManager().tasks.put(task.name, task);
    }

    public List<GlobalTask> getGlobalTasks() {
        return this.globalTaskPool;
    }

    public List<TaskInfo> getPlayerTaskPool() {
        return this.playerTaskPool;
    }

    public boolean equals(Role other) {
        return other.name.equals(this.name);
    }
}

