package io.github.mjk134.titanomach.server.menu;

import net.minecraft.entity.player.PlayerEntity;

public interface MenuClickAction {
    void onClick(PlayerEntity player, int slot, MenuScreenHandler menuContext);
}
