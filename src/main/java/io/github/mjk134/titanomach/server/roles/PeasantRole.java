package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.server.tasks.Task;

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
    }

    @Override
    public Task[] getGlobalTasks() {
        return new Task[] {};
    }
}
