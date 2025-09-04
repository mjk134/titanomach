package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.server.tasks.*;

public class PeasantRole extends Role {
    public PeasantRole() {
        super(
                "Peasant",
                """
                        §7The downtrodden masses, fighting to survive.
                        """,
                "minecraft:wheat",
                "§7",
                0
        );
        addEffect("minecraft:weakness", 1);

        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:andesite", 3*64, 350));      // mid
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:cobblestone", 3*64, 150));   // mid
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:wheat", 2*64, 200));         // easy
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:apple", 12, 250));            // easy
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:sheep", 20, 350));               // mid
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:pig", 20, 350));                 // mid
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:zombie", 20, 350));              // easy-mid
        addPlayerTask(new TaskInfo(TaskType.ADVANCEMENT, "minecraft:story/upgrade_tools", 1, 150)); // quick
        addPlayerTask(new TaskInfo(TaskType.ADVANCEMENT, "minecraft:story/iron_tools", 1, 150));    // quick
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:ink_sac", 48, 450));         // mid
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:potato", 2*64, 550));        // easy-mid
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:chicken", 30, 500));             // mid
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:feather", 32, 400));         // easy-mid
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:slime", 12, 550));                // rarer

        addGlobalTask(new GlobalCollectionTask("peasant-global-1", 3*64, 200, "minecraft:dirt"));
        addGlobalTask(new GlobalSlayerTask("peasant-global-2", 20, 400, "minecraft:spider"));       // combat
        addGlobalTask(new GlobalCollectionTask("peasant-global-3", 15*64, 350, "minecraft:carrot")); // farm

    }
}
