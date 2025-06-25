package io.github.mjk134.titanomach.server.menu;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface MenuClickAction {
    public void onClick(PlayerEntity player, int slot, ItemStack itemStack);
}
