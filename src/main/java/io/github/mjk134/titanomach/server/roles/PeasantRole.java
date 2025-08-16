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

        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:andesite", 5*64, 350));      // mid
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:cobblestone", 5*64, 350));   // mid
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:wheat", 2*64, 200));         // easy
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:apple", 8, 150));            // easy
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:sheep", 20, 250));               // mid
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:pig", 20, 250));                 // mid
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:zombie", 10, 200));              // easy-mid
        addPlayerTask(new TaskInfo(TaskType.ADVANCEMENT, "minecraft:story/upgrade_tools", 1, 150)); // quick
        addPlayerTask(new TaskInfo(TaskType.ADVANCEMENT, "minecraft:story/iron_tools", 1, 150));    // quick
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:ink_sac", 48, 300));         // mid
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:potato", 2*64, 250));        // easy-mid
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:chicken", 30, 250));             // mid
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:feather", 32, 200));         // easy-mid
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:slime", 12, 300));                // rarer

        addGlobalTask(new GlobalCollectionTask("peasant-global-1", 27*64, 500, "minecraft:dirt"));
        addGlobalTask(new GlobalSlayerTask("peasant-global-2", 200, 400, "minecraft:spider"));       // combat
        addGlobalTask(new GlobalCollectionTask("peasant-global-3", 15*64, 350, "minecraft:carrot")); // farm

    }
}
