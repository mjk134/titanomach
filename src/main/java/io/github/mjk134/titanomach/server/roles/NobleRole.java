package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.server.tasks.GlobalCollectionTask;
import io.github.mjk134.titanomach.server.tasks.GlobalSlayerTask;
import io.github.mjk134.titanomach.server.tasks.TaskInfo;
import io.github.mjk134.titanomach.server.tasks.TaskType;

public class NobleRole extends Role {
    public NobleRole() {
        super("Noble", "filler", "minecraft:diamond", "§b", 25000);
        addRankUpReward("minecraft:emerald", 64);
        addRankUpReward("minecraft:diamond", 32);
        addEffect("minecraft:speed", 1);
        addEffect("minecraft:resistance", 1);


        // Player Tasks (~40,000 pts total)
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:netherite_ingot", 8, 4000));        // grind
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:beacon", 2, 5000));                 // huge build
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:wither", 2, 5000));                     // 2 Withers
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:shulker_shell", 32, 3000));         // end raiding
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:shulker", 16, 3000));                   // paired
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:elytra", 2, 4000));                 // multiple end cities
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:dragon_breath", 64, 3000));         // grind trips
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:ancient_debris", 32, 4000));        // deep mining
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:evoker", 5, 3500));                     // raid leaders
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:totem_of_undying", 3, 3500));       // raid rewards
        addPlayerTask(new TaskInfo(TaskType.ADVANCEMENT, "minecraft:end/dragon_egg", 1, 4000));        // prestige
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:enchanted_golden_apple", 2, 4500)); // VERY rare
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:heart_of_the_sea", 2, 3000));       // treasure hunt
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:warden", 2, 5000));                     // endgame mob

        // Global Tasks (~10,000 pts total)
        addGlobalTask(new GlobalCollectionTask("noble-global-1", 32*64, 4000, "minecraft:emerald_block")); // group wealth
        addGlobalTask(new GlobalSlayerTask("noble-global-2", 500, 3000, "minecraft:phantom"));             // late-game grind
        addGlobalTask(new GlobalCollectionTask("noble-global-3", 20*64, 3000, "minecraft:netherite_scrap"));// deep grind

    }
}
