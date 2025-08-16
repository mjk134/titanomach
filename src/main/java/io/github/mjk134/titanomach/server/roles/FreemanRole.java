package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.server.tasks.GlobalCollectionTask;
import io.github.mjk134.titanomach.server.tasks.GlobalSlayerTask;
import io.github.mjk134.titanomach.server.tasks.TaskInfo;
import io.github.mjk134.titanomach.server.tasks.TaskType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class FreemanRole extends Role {
    public FreemanRole() {
        super(
                "Freeman",
                "filler",
                "minecraft:orange_wool",
                "§6",
                6250
        );

        addRankUpReward("minecraft:golden_apple", 8);
        addRankUpReward("minecraft:diamond", 4);
        addEffect("minecraft:haste", 1);
        addEffect("minecraft:luck", 1);

        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:oak_log", 6*64, 700));               // mid
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:iron_ingot", 64, 800));              // harder
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:bread", 128, 500));                  // mid
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:honey_bottle", 16, 700));            // rare
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:cow", 30, 600));                         // mid
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:creeper", 25, 700));                     // harder combat
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:skeleton", 25, 700));                    // harder combat
        addPlayerTask(new TaskInfo(TaskType.ADVANCEMENT, "minecraft:story/smelt_iron", 1, 300));        // easy
        addPlayerTask(new TaskInfo(TaskType.ADVANCEMENT, "minecraft:story/deflect_arrow", 1, 400));
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:pumpkin", 64, 500));                 // mid
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:melon_slice", 128, 500));            // mid
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:enderman", 15, 600));                     // rare spawn
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:leather", 64, 500));                 // from cows
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:witch", 3, 600));                        // rare mob

        addGlobalTask(new GlobalCollectionTask("freeman-global-1", 5*64, 1000, "minecraft:coal"));     // mining
        addGlobalTask(new GlobalSlayerTask("freeman-global-2", 150, 800, "minecraft:drowned"));         // combat
        addGlobalTask(new GlobalCollectionTask("freeman-global-3", 5*64, 700, "minecraft:beetroot"));  // farming
    }

    @Override
    public void onEffectTick(PlayerEntity player) {
        // Remove the weakness from Peasant
        player.removeStatusEffect(Registries.STATUS_EFFECT.getEntry(Identifier.of("minecraft:weakness")).get());
        super.onEffectTick(player);
    }
}
