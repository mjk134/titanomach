package io.github.mjk134.titanomach.server.tasks;

import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static io.github.mjk134.titanomach.Titanomach.ModLogger;



public class Task {
    public String name;
    private int progress;
    private int maxProgress;

    public Task(String name, int maxProgress) {
        this.name = name;
        this.maxProgress = maxProgress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void OpenTaskMenu(ServerPlayerEntity player) {
        ModLogger.info("Opening TaskMenu");
        player.openHandledScreen( new SimpleNamedScreenHandlerFactory( (i, playerInventory, playerEntity) -> GenericContainerScreenHandler.createGeneric9x3( i, playerInventory), Text.of("container.modname.name")));
    }
}
