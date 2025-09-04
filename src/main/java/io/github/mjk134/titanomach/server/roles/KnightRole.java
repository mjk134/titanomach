package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.server.tasks.GlobalCollectionTask;
import io.github.mjk134.titanomach.server.tasks.GlobalSlayerTask;
import io.github.mjk134.titanomach.server.tasks.TaskInfo;
import io.github.mjk134.titanomach.server.tasks.TaskType;

public class KnightRole extends Role{
    public KnightRole() {
        super("Knight", "filler", "minecraft:iron_sword", "§a", 12500);


        addRankUpReward("minecraft:netherite_upgrade_smithing_template", 1);
        addEffect("minecraft:strength", 1);

        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:blaze", 30, 1800));                   // Nether combat
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:wither_skeleton", 15, 2000));          // Rare combat
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:guardian", 20, 1500));                // Ocean exploration
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:gold_ingot", 128, 1000));         // easy
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:lapis_lazuli", 256, 1000));       // mining
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:cooked_beef", 256, 1200));        // farming/combat
        // addPlayerTask(new TaskInfo(TaskType.ADVANCEMENT, "minecraft:nether/return_to_sender", 1, 1500)); // idk
        addPlayerTask(new TaskInfo(TaskType.ADVANCEMENT, "minecraft:end/kill_dragon", 1, 2500));     // boss mob
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:ender_pearl", 32, 800));         // rare mob drop
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:ghast_tear", 8, 1200));           // rare drop
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:ravager", 10, 2000));                  // raid mob
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:totem_of_undying", 1, 2000));     // raid reward
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:netherite_scrap", 4, 1500));      // late-game mining
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:elder_guardian", 1, 2500));           // boss mob

        addGlobalTask(new GlobalCollectionTask("knight-global-1", 2*64, 2000, "minecraft:diamond"));    // mining
        addGlobalTask(new GlobalSlayerTask("knight-global-2", 200, 1500, "minecraft:pillager"));         // raids
        addGlobalTask(new GlobalCollectionTask("knight-global-3", 2*64, 1500, "minecraft:emerald"));    // trading/mining


    }
}
