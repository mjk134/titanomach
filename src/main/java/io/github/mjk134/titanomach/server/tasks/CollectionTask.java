package io.github.mjk134.titanomach.server.tasks;

import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class CollectionTask extends Task{
    private String targetItem;

    public CollectionTask(String name, int maxProgress, String targetItem) {
        super(name, maxProgress);
        this.targetItem = targetItem;
    }

    @Override
    public void OpenTaskMenu(ServerPlayerEntity player) {
        NamedScreenHandlerFactory screen = new SimpleNamedScreenHandlerFactory( (i, playerInventory, playerEntity) -> GenericContainerScreenHandler.createGeneric9x3( i, playerInventory), Text.of("COLLECTION"));
        player.openHandledScreen(screen);
    }
}
