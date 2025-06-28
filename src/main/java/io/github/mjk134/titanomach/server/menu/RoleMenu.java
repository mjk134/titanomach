package io.github.mjk134.titanomach.server.menu;

import com.mojang.authlib.GameProfile;
import io.github.mjk134.titanomach.Titanomach;
import io.github.mjk134.titanomach.server.TitanomachPlayer;
import io.github.mjk134.titanomach.server.roles.Role;
import io.github.mjk134.titanomach.server.roles.RoleManager;
import io.github.mjk134.titanomach.utils.ItemBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class RoleMenu extends Menu {
    // this probably needs to change later
    public RoleMenu(PlayerEntity player) {
        super("Roles");

        TitanomachPlayer tPlayer = Titanomach.TITANOMACH_CONFIG.getPlayerConfig((ServerPlayerEntity) player);
        Role playerRole = RoleManager.getPlayerRole(tPlayer);

        addRoleIcon(RoleManager.getRole("Peasant"), 1, playerRole.name);
        addRoleIcon(RoleManager.getRole("Freeman"), 2, playerRole.name);
        addRoleIcon(RoleManager.getRole("Knight"), 3, playerRole.name);
        addRoleIcon(RoleManager.getRole("Noble"), 5, playerRole.name);
        addRoleIcon(RoleManager.getRole("King"), 6, playerRole.name);
        addRoleIcon(RoleManager.getRole("God"), 7, playerRole.name);

        addProgressBar(tPlayer);

        GameProfile gameProfile = player.getGameProfile();
        ItemStack playerHead = new ItemBuilder("minecraft:player_head")
                .setName("§d" + gameProfile.getName())
                .addLoreLine("§7Rank: " + playerRole.titleFormat + "§l" + playerRole.name)
                .addLoreLine("§7Total §aPP: §e" + tPlayer.getProgressPoints())
                .create();
        playerHead.set(DataComponentTypes.PROFILE, new ProfileComponent(gameProfile));
        setItem(22, playerHead);

        fillEmptyWithGlass();
    }

    private void addProgressBar(TitanomachPlayer player) {
        float progress = RoleManager.getPercentageProgressToNextRole(player);
        int numberOfGreen = (int) (progress * 7);
        Role currentRole =  RoleManager.getPlayerRole(player);
        Role nextRole = RoleManager.getNextRole(currentRole);
        for (int i = 0; i < 7; i++) {
            String itemID = "";
            if (i < numberOfGreen) {
                itemID = "minecraft:lime_stained_glass_pane";
            }
            else if (i == numberOfGreen) {
                itemID = "minecraft:yellow_stained_glass_pane";
            }
            else {
                itemID = "minecraft:red_stained_glass_pane";
            }

            ItemStack itemIcon;
            if (nextRole != null) {
                int progressIntoRank = player.getProgressPoints() - currentRole.pointRequirement;
                int totalNeeded = nextRole.pointRequirement - currentRole.pointRequirement;
                itemIcon = new ItemBuilder(itemID)
                        .setName("§cProgress to " + nextRole.titleFormat + "§l" + nextRole.name)
                        .addLoreLine("§e" + progressIntoRank + "§6/§e" + totalNeeded + " §aPP")
                        .create();
            } else {
                itemIcon = new ItemBuilder(itemID)
                        .setName("§c§lMAX ROLE")
                        .create();
            }

            this.setItem(10 + i, itemIcon);
        }
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
