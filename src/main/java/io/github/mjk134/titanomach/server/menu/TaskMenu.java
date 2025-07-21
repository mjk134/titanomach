package io.github.mjk134.titanomach.server.menu;

import com.mojang.authlib.GameProfile;
import io.github.mjk134.titanomach.Titanomach;
import io.github.mjk134.titanomach.server.TitanomachPlayer;
import io.github.mjk134.titanomach.server.roles.Role;
import io.github.mjk134.titanomach.server.roles.RoleManager;
import io.github.mjk134.titanomach.server.tasks.*;
import io.github.mjk134.titanomach.utils.ItemBuilder;
import io.github.mjk134.titanomach.utils.TextUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.List;

public class TaskMenu extends Menu {
    // this probably needs to change later
    public TaskMenu(PlayerEntity player) {
        super("Tasks");
        TitanomachPlayer tPlayer = Titanomach.TITANOMACH_CONFIG.getPlayerConfig((ServerPlayerEntity) player);

        addRoles(tPlayer);
        addProgressBar(tPlayer);
        addPlayerHead(player);
        addPlayerTasks(tPlayer);

        fillEmptyWithGlass();
    }

    private void addRoles(TitanomachPlayer tPlayer) {
        Role playerRole = RoleManager.getPlayerRole(tPlayer);
        addRoleIcon(RoleManager.getRole("Peasant"), 1, playerRole.name);
        addRoleIcon(RoleManager.getRole("Freeman"), 2, playerRole.name);
        addRoleIcon(RoleManager.getRole("Knight"), 3, playerRole.name);
        addRoleIcon(RoleManager.getRole("Noble"), 5, playerRole.name);
        addRoleIcon(RoleManager.getRole("King"), 6, playerRole.name);
        addRoleIcon(RoleManager.getRole("God"), 7, playerRole.name);
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
            String itemID;
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

    private void addPlayerTasks(TitanomachPlayer tPlayer) {
        Role role = RoleManager.getPlayerRole(tPlayer);
        TaskManager taskManager = Titanomach.TITANOMACH_CONFIG.getTaskManager();
        Task currentTask = taskManager.getTaskFromPlayer(tPlayer);
        boolean playerHasTask = currentTask != null;

        List<TaskInfo> tasks = role.getPlayerTaskPool();
        for (int i = 0; i<7; i++) {
            ItemBuilder taskIconBuilder = new ItemBuilder("minecraft:map");
            MenuClickAction clickAction = MenuClickAction.NO_ACTION;
            if (i < tasks.size()) {
                TaskInfo taskInfo = tasks.get(i);
                boolean thisTaskSelected = playerHasTask && taskInfo.equals(currentTask);

                // add title
                String activeModifier = thisTaskSelected ? " §8(Active)" : "";
                String targetName = switch (taskInfo.taskType) {
                    case COLLECTION -> TextUtils.itemIDtoName(taskInfo.target);
                    case SLAYER -> TextUtils.entityIDtoName(taskInfo.target);
                };
                taskIconBuilder.setName("§6§l" + TaskType.presentVerb(taskInfo.taskType).toUpperCase() + "§r§f " + taskInfo.maxProgress + " " + targetName + activeModifier);

                // add pp reward
                taskIconBuilder.addLoreLine("§7Grants §e" + taskInfo.progressPointReward + " §aPP");

                // add click tooltip if no task is selected
                if (!playerHasTask) {
                    taskIconBuilder.addLoreMultiline("\n§9Click to select this task!");
                    // select event
                    clickAction = (player, slot, menuContext) -> {
                        String playerID = tPlayer.getPlayerId();
                        Task task = taskInfo.createTask(playerID);
                        taskManager.addTask(task, playerID);
                        task.updateProgress((ServerPlayerEntity) player);
                        addPlayerTasks(tPlayer);
                        player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.UI, 1.0f, 1.0f);
                    };
                } else {
                    if (thisTaskSelected) {
                        taskIconBuilder.setItem("minecraft:filled_map");
                        taskIconBuilder.setEnchanted(true);

                        taskIconBuilder.addLoreMultiline("\n§e" + currentTask.progress + "§6/§e" + currentTask.maxProgress + " §7" + targetName + " " + TaskType.pastVerb(taskInfo.taskType));
                        taskIconBuilder.addLoreLine(TextUtils.progressBar(16, (float) currentTask.progress / currentTask.maxProgress, true));

                        // String submitText = currentTask.progress == currentTask.maxProgress ? "§9Click to submit task" : "§c§oNot enough items to complete task!";
                        if (currentTask.progress >= currentTask.maxProgress) {
                            taskIconBuilder.addLoreMultiline("\n§9Click to submit task");
                        }

                        clickAction = (player, slot, menuContext) -> {
                            boolean success = taskManager.submitTask(currentTask.name, (ServerPlayerEntity) player);
                            if (success) {
                                player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.UI, 1.0f, 1.0f);
                            }
                            // update ui
                            addRoles(tPlayer);
                            addPlayerTasks(tPlayer);
                            addProgressBar(tPlayer);
                            addPlayerHead(player);
                        };
                    } else {
                        taskIconBuilder.addLoreMultiline("\n§c§oYou have already selected another task!");
                    }
                }
            }
            else {
                taskIconBuilder.setName("Empty Task");
            }
            setClickableItem(28 + i, taskIconBuilder.create(), clickAction);
        }
    }
}
