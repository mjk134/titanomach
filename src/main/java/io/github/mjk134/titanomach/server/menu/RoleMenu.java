package io.github.mjk134.titanomach.server.menu;

import com.mojang.authlib.GameProfile;
import io.github.mjk134.titanomach.Titanomach;
import io.github.mjk134.titanomach.server.TitanomachPlayer;
import io.github.mjk134.titanomach.server.roles.Role;
import io.github.mjk134.titanomach.server.roles.RoleManager;
import io.github.mjk134.titanomach.utils.ItemBuilder;
import io.github.mjk134.titanomach.utils.TextUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

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
        addPlayerHead(player);

        fillEmptyWithGlass();
    }

    private void addPlayerHead(PlayerEntity player) {
        TitanomachPlayer tPlayer = Titanomach.TITANOMACH_CONFIG.getPlayerConfig((ServerPlayerEntity) player);
        Role playerRole = RoleManager.getPlayerRole(tPlayer);
        GameProfile gameProfile = player.getGameProfile();

        ItemBuilder playerHeadBuilder = new ItemBuilder("minecraft:player_head")
                .setName("§d" + gameProfile.getName())
                .addLoreLine("§7Rank: " + playerRole.titleFormat + "§l" + playerRole.name)
                .addLoreLine("§7Total §aPP: §e" + tPlayer.getProgressPoints());

        ItemStack playerHead = playerHeadBuilder.create();
        playerHead.set(DataComponentTypes.PROFILE, new ProfileComponent(gameProfile));
        setItem(22, playerHead);
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
                ItemBuilder builder = new ItemBuilder(itemID)
                        .setName("§cProgress to " + nextRole.titleFormat + "§l" + nextRole.name)
                        .addLoreLine("§e" + progressIntoRank + "§6/§e" + totalNeeded + " §aPP")
                        .addLoreLine("");

                // add effect info
                generateEffectInfo(builder, nextRole);

                // add rank-up reward info
                builder.addLoreMultiline("\n§9Rewards");
                for (ItemStack itemStack : nextRole.getRankUpRewards()) {
                    String qtyText = itemStack.getCount() > 1 ? " §7x" +  itemStack.getCount() : "";
                    String itemName = itemStack.getName().getString();
                    itemName = TextUtils.removeFormatting(itemName);
                    builder.addLoreLine("§7• " + itemName + qtyText);
                }

                itemIcon = builder.create();
            } else {
                itemIcon = new ItemBuilder(itemID)
                        .setName("§c§lMAX ROLE")
                        .create();
            }

            this.setItem(10 + i, itemIcon);
        }
    }

    private void generateEffectInfo(ItemBuilder builder, Role role) {
        builder.addLoreMultiline("§9Effects");
        for (StatusEffectInstance effect : role.getEffects()) {
            String level = TextUtils.toRomanNumerals(effect.getAmplifier() + 1);
            String name = Text.translatable(effect.getTranslationKey()).getString();
            builder.addLoreLine("§7• " + name + " " + level);
        }
        if (!(role.name.equals("Freeman")||role.name.equals("Peasant"))) {
            builder.addLoreLine("§7+ Effects from previous roles");
        }
    }

    private void addRoleIcon(Role role, int slot, String playerRole) {
        ItemBuilder roleIconBuilder = new ItemBuilder(role.itemIcon)
                .setName(role.titleFormat + "§l" + role.name)
                .removeAdditionalTooltips()
                .addLoreMultiline(role.description)
                .addLoreLine("");

        generateEffectInfo(roleIconBuilder, role);
        ItemStack roleIcon = roleIconBuilder.create();

        if (role.name.equals(playerRole)) {
            roleIcon.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        this.setItem(slot, roleIcon);
    }
}
