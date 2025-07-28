package io.github.mjk134.titanomach.server.menu;

import io.github.mjk134.titanomach.Titanomach;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.SetPlayerInventoryS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;

public class MenuScreenHandler extends GenericContainerScreenHandler {
    private final Menu menu;
    public MenuScreenHandler(int syncId, PlayerInventory playerInventory, Menu menu) {
        super(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, menu.getInventory(), 6);
        this.menu = menu;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        // override the default slot click function to do nothing so items can't be taken out of menus

        // if enabled, we still want to keep default behaviour in inventories
        if ((slotIndex >= 54 || slotIndex == -999) && menu.keepDefaultInventoryBehaviour)
            super.onSlotClick(slotIndex, button, actionType, player);

        // if valid action type is performed, call click callback
        // this check prevents the callback from sometimes being called twice from a PICKUP_ALL action
        if (actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_MOVE || actionType == SlotActionType.SWAP || actionType == SlotActionType.THROW) {
            MenuClickAction action = menu.getAction(slotIndex);
            if (action != null)
                action.onClick(player, slotIndex, this);
        }

        // if offhand swap used, we need to restore the previous item as
        // for some reason this is handled differently to a hotbar swap
        ItemStack playerOffhandItem = player.getOffHandStack();
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
        serverPlayerEntity.networkHandler.sendPacket(new SetPlayerInventoryS2CPacket(40, playerOffhandItem));
    }

    @Override
    public void onClosed(PlayerEntity player) {
        this.menu.onClose(player, this);
        super.onClosed(player);
    }

    public Menu getMenu() {
        return this.menu;
    }

    public void close(PlayerEntity player) {
        ((ServerPlayerEntity) player).closeHandledScreen();
    }
}
