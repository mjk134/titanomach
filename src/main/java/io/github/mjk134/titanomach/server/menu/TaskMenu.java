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
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.List;

public class TaskMenu extends Menu {
    private final PlayerEntity player;
    private final TitanomachPlayer tPlayer;

    public TaskMenu(PlayerEntity player) {
        super("Tasks");
        this.player = player;
        this.tPlayer = Titanomach.TITANOMACH_CONFIG.getPlayerConfig((ServerPlayerEntity) player);
        refreshUI();
        fillEmptyWithGlass();
    }

    private void refreshUI() {
        addRoles();
        addProgressBar();
        addPlayerHead();
        addPlayerTasks();
        addGlobalTasks();
    }

    private void addRoles() {
        Role playerRole = RoleManager.getPlayerRole(tPlayer);
        addRoleIcon(RoleManager.getRole("Peasant"), 1, playerRole.name);
        addRoleIcon(RoleManager.getRole("Freeman"), 2, playerRole.name);
        addRoleIcon(RoleManager.getRole("Knight"), 3, playerRole.name);
        addRoleIcon(RoleManager.getRole("Noble"), 5, playerRole.name);
        addRoleIcon(RoleManager.getRole("King"), 6, playerRole.name);
        addRoleIcon(RoleManager.getRole("God"), 7, playerRole.name);
    }

    private void addPlayerHead() {
        Role playerRole = RoleManager.getPlayerRole(tPlayer);
        GameProfile gameProfile = player.getGameProfile();

        ItemBuilder playerHeadBuilder = new ItemBuilder("minecraft:player_head")
                .setName("§d" + gameProfile.getName())
                .addLoreLine("§7Rank: " + playerRole.titleFormat + "§l" + playerRole.name)
                .addLoreLine("§7Total §aPP: §e" + tPlayer.getProgressPoints());

        ItemStack playerHead = playerHeadBuilder.create();
        playerHead.set(DataComponentTypes.PROFILE, new ProfileComponent(gameProfile));
        setItem(53, playerHead);
    }

    private void addProgressBar() {
        float progress = RoleManager.getPercentageProgressToNextRole(tPlayer);
        int numberOfGreen = (int) (progress * 7);
        Role currentRole =  RoleManager.getPlayerRole(tPlayer);
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
                int progressIntoRank = tPlayer.getProgressPoints() - currentRole.pointRequirement;
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

    private void addPlayerTasks() {
        Role role = RoleManager.getPlayerRole(tPlayer);
        TaskManager taskManager = Titanomach.TITANOMACH_CONFIG.getTaskManager();
        Task currentTask = taskManager.getTaskFromPlayer(tPlayer);
        boolean playerHasTask = currentTask != null;

        List<TaskInfo> tasks = role.getPlayerTaskPool();
        for (int i = 0; i<14; i++) {
            ItemBuilder taskIconBuilder = new ItemBuilder("minecraft:map");
            MenuClickAction clickAction = MenuClickAction.NO_ACTION;
            if (i < tasks.size()) {
                TaskInfo taskInfo = tasks.get(i);
                Task possibleTask = taskInfo.createTask(tPlayer.getPlayerId());
                boolean thisTaskSelected = playerHasTask && taskInfo.equals(currentTask);

                // add title
                String activeModifier = thisTaskSelected ? " §8(Active)" : "";
                taskIconBuilder.setName(possibleTask.getFormattedName() + activeModifier);

                // add pp reward
                taskIconBuilder.addLoreLine("§7Grants §e" + taskInfo.progressPointReward + " §aPP");

                // add click tooltip if no task is selected
                if (!playerHasTask) {
                    if (!taskManager.completedPlayerTasks.contains(possibleTask.name)) {
                        taskIconBuilder.addLoreMultiline("\n§9Click to select this task!");
                        // select event
                        clickAction = (player, slot, menuContext) -> {
                            String playerID = tPlayer.getPlayerId();
                            taskManager.addTask(possibleTask, playerID);
                            player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.UI, 1.0f, 1.0f);
                            refreshUI();
                        };
                    }
                    else {
                        taskIconBuilder.addLoreMultiline("\n§c§oThis task has already been completed!");
                    }
                } else {
                    if (thisTaskSelected) {
                        taskIconBuilder.setItem("minecraft:filled_map");
                        taskIconBuilder.setEnchanted(true);
                        taskIconBuilder.addLoreMultiline("\n" + getProgressString(currentTask));

                        if (currentTask.progress >= currentTask.maxProgress) {
                            taskIconBuilder.addLoreMultiline("\n§9Click to submit task");
                        } else if (currentTask instanceof CollectionTask collectionTask && collectionTask.getInventoryCount((ServerPlayerEntity) player) > 0) {
                            taskIconBuilder.addLoreMultiline("\n§9Click to submit current items");
                        }
                        clickAction = (player, slot, menuContext) -> {
                            SubmitStatus status = taskManager.submitTask(currentTask.name, (ServerPlayerEntity) player);
                            SoundEvent sound = switch (status) {
                                case COMPLETED -> SoundEvents.ENTITY_PLAYER_LEVELUP;
                                case PARTIAL -> SoundEvents.BLOCK_NOTE_BLOCK_PLING.value();
                                case FAIL -> SoundEvents.BLOCK_ANVIL_PLACE;
                            };
                            player.playSoundToPlayer(sound, SoundCategory.UI, 1.0f, 1.0f);
                            refreshUI();
                        };
                    } else {
                        taskIconBuilder.addLoreMultiline("\n§c§oYou have already selected another task!");
                    }
                }
            }
            else {
                taskIconBuilder.setName("Empty Task");
            }
            int slot = i < 7 ? 28 + i : 30 + i;
            setClickableItem(slot, taskIconBuilder.create(), clickAction);
        }
    }

    public void addGlobalTasks() {
        List<GlobalTask> tasks = RoleManager.getPlayerRole(tPlayer).getGlobalTasks();
        TaskManager taskManager = Titanomach.TITANOMACH_CONFIG.getTaskManager();

        for (int i = 0; i < 3; i++) {
            ItemBuilder taskIconBuilder = new ItemBuilder("minecraft:painting");
            MenuClickAction clickAction = MenuClickAction.NO_ACTION;
            if (i < tasks.size()) {
                GlobalTask task = tasks.get(i);

                // add title
                taskIconBuilder.setName(task.getFormattedName() + " §7(§d§lGLOBAL \uD83C\uDF0E§7)");
                // add pp reward
                taskIconBuilder.addLoreLine("§7Grants §e" + task.progressPointReward + " §aPP" + "§7 across all contributors");
                // add progress bar
                taskIconBuilder.addLoreLine("\n" + getProgressString(task));

                boolean completed = taskManager.completedGlobalTasks.contains(task.name);
                if (!completed) {
                    taskIconBuilder.setEnchanted(true);
                    taskIconBuilder.addLoreMultiline("\n§7You have contributed §e" + task.getPlayerContribution(tPlayer.getPlayerId()) + " §7" + task.getTargetDisplayName());
                    taskIconBuilder.addLoreLine("§7You will receive §e" + task.getPlayerContributionAsProgressPoints(tPlayer.getPlayerId()) + " §aPP §7upon completion");
                    taskIconBuilder.addLoreMultiline("\n§9Click to contribute items");

                    clickAction = (player, slot, menuContext) -> {
                        SubmitStatus status = taskManager.submitTask(task.name, (ServerPlayerEntity) player);;
                        if (status == SubmitStatus.PARTIAL || status == SubmitStatus.COMPLETED) {
                            player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.UI, 1.0f, 1.0f);
                            refreshUI();
                        } else {
                            player.playSoundToPlayer(SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.UI, 1.0f, 1.0f);
                        }
                    };
                } else {
                    taskIconBuilder.addLoreMultiline("\n§c§oThis task has already been completed!");
                }
            }
            else {
                taskIconBuilder.setName("§fEmpty Task");
            }
            setClickableItem(21 + i, taskIconBuilder.create(), clickAction);
        }
    }

    private String getProgressString(Task task) {
        String result = "§e" + task.progress + "§6/§e" + task.maxProgress + " §7" + task.getTargetDisplayName() + " " + TaskType.pastVerb(TaskType.get(task));

        if (task instanceof CollectionTask collectionTask) {
            result += "\n" + TextUtils.progressBarWithOptimisticProgress(
                    16,
                    task.getPercentageProgress(),
                    true,
                    collectionTask.getOptimisticPercentageProgress((ServerPlayerEntity) player)
            );
        }
        else {
            result += "\n" + TextUtils.progressBar(16, task.getPercentageProgress(),true);
        }
        return result;
    }
}
