package io.github.mjk134.titanomach.server.menu;

import net.minecraft.entity.player.PlayerEntity;

public interface MenuClickAction {
    MenuClickAction NO_ACTION = (player, slot, menuContext) -> {};
    void onClick(PlayerEntity player, int slot, MenuScreenHandler menuContext);
}
