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
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:wheat", 6*64, 1500));
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:carrot", 8*64, 1500));
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:potato", 8*64, 1500));
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:warden", 2, 5000));
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:melon_slice", 10*64, 1500));

        // Villager trade grind
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:bookshelf", 5*64, 3000));       // librarian trades
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:glass", 5*64, 2500));          // cleric/cartographer
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:golden_carrot", 3*64, 2500));   // farmer trade
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:wither", 4, 4000));     // cleric trade
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:experience_bottle", 64, 4000)); // late-game cleric

        // Mob grind
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:cow", 200, 1000));
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:chicken", 300, 1000));
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:pillager", 45, 1500));             // raid mobs
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:totem_of_undying", 5, 2500));   // raid rewards

        // Global Tasks (~10,000 pts total)
        addGlobalTask(new GlobalCollectionTask("noble-global-1", 8*64, 7000, "minecraft:emerald"));       // collective trading grind
        addGlobalTask(new GlobalCollectionTask("noble-global-2", 25, 1000, "minecraft:enchanted_book"));// enchanted books bulk
        addGlobalTask(new GlobalSlayerTask("noble-global-3", 20, 2500, "minecraft:evoker"));

    }
}
