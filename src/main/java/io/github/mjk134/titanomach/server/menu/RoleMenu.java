package io.github.mjk134.titanomach.server.menu;

import io.github.mjk134.titanomach.Titanomach;
import io.github.mjk134.titanomach.server.roles.Role;
import io.github.mjk134.titanomach.server.roles.RoleManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class RoleMenu extends Menu {
    // this probably needs to change later
    public RoleMenu(PlayerEntity player) {
        super("Roles");

        String playerRole = Titanomach.TITANOMACH_CONFIG.getPlayerConfig(player.getUuidAsString()).getRoleName();

        addRoleIcon(RoleManager.getRole("Peasant"), 1, playerRole);
        addRoleIcon(RoleManager.getRole("Freeman"), 2, playerRole);
        addRoleIcon(RoleManager.getRole("Knight"), 3, playerRole);
        addRoleIcon(RoleManager.getRole("Noble"), 5, playerRole);
        addRoleIcon(RoleManager.getRole("King"), 6, playerRole);
        addRoleIcon(RoleManager.getRole("God"), 7, playerRole);

        fillEmptyWithGlass();
    }

    private void addRoleIcon(Role role, int slot, String playerRole) {
        ItemStack roleIcon = new ItemStack(role.itemIcon);
        MutableText title = Text.literal("§r" + role.titleFormat + role.name);
        roleIcon.set(DataComponentTypes.CUSTOM_NAME, title);
        roleIcon.remove(DataComponentTypes.ATTRIBUTE_MODIFIERS);


        List<Text> lines = new ArrayList<>();
        LoreComponent lore = new LoreComponent(lines);
        for (String line : role.description.split("\n")) {
            lines.add(Text.of(line));
        }
        roleIcon.set(DataComponentTypes.LORE, lore);

        if (role.name.equals(playerRole)) {
            roleIcon.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        this.setItem(slot, roleIcon);
    }
}
