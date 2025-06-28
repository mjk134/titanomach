package io.github.mjk134.titanomach.server.menu;

import com.mojang.authlib.GameProfile;
import io.github.mjk134.titanomach.Titanomach;
import io.github.mjk134.titanomach.server.roles.Role;
import io.github.mjk134.titanomach.server.roles.RoleManager;
import io.github.mjk134.titanomach.utils.ItemBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class RoleMenu extends Menu {
    // this probably needs to change later
    public RoleMenu(PlayerEntity player) {
        super("Roles");

        Role playerRole = RoleManager.getRole(Titanomach.TITANOMACH_CONFIG.getPlayerConfig(player.getUuidAsString()).getRoleName());

        addRoleIcon(RoleManager.getRole("Peasant"), 1, playerRole.name);
        addRoleIcon(RoleManager.getRole("Freeman"), 2, playerRole.name);
        addRoleIcon(RoleManager.getRole("Knight"), 3, playerRole.name);
        addRoleIcon(RoleManager.getRole("Noble"), 5, playerRole.name);
        addRoleIcon(RoleManager.getRole("King"), 6, playerRole.name);
        addRoleIcon(RoleManager.getRole("God"), 7, playerRole.name);


        GameProfile gameProfile = player.getGameProfile();
        ItemStack playerHead = new ItemBuilder("minecraft:player_head")
                .setName("§d" + gameProfile.getName())
                .addLoreLine("§7Rank: " + playerRole.titleFormat + "§l" + playerRole.name)
                .create();
        playerHead.set(DataComponentTypes.PROFILE, new ProfileComponent(gameProfile));
        setItem(22, playerHead);

        fillEmptyWithGlass();
    }

    private void addRoleIcon(Role role, int slot, String playerRole) {
        ItemStack roleIcon = new ItemBuilder(role.itemIcon)
                .setName(role.titleFormat + "§l" + role.name)
                .removeAdditionalTooltips()
                .addLoreMultiline(role.description)
                .create();

        if (role.name.equals(playerRole)) {
            roleIcon.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        this.setItem(slot, roleIcon);
    }
}
