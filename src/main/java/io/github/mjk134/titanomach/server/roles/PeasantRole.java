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

        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:andesite", 5*64, 512));
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:cobblestone", 5*64, 512));
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:wheat", 2*64, 256));
        addPlayerTask(new TaskInfo(TaskType.COLLECTION, "minecraft:apple", 8, 128));
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:sheep", 20, 128));
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:pig", 20, 128));
        addPlayerTask(new TaskInfo(TaskType.SLAYER, "minecraft:zombie", 10, 128));
        addPlayerTask(new TaskInfo(TaskType.ADVANCEMENT, "minecraft:story/upgrade_tools",1, 128));

        addGlobalTask(new GlobalCollectionTask("peasant-global-1", 27*64, 720, "minecraft:dirt"));
        addGlobalTask(new GlobalSlayerTask("peasant-global-2", 5, 100, "minecraft:zombie"));

    }
}
