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
    private final ServerPlayerEntity player;
    private final TitanomachPlayer tPlayer;
    private final TaskManager taskManager;

    public TaskMenu(PlayerEntity player) {
        super("Tasks");
        this.player = (ServerPlayerEntity) player;
        this.tPlayer = Titanomach.CONFIG.getPlayerConfig((ServerPlayerEntity) player);
        this.taskManager = Titanomach.CONFIG.getTaskManager();
        refreshUI();
    }

    private void refreshUI() {
        clear();
        addRoles();
        addPlayerHead();
        addPlayerTasks();
        addGlobalTasks();
        addCancelButton();
        fillEmptyWithGlass();
    }

    private void addRoles() {
        addRoleIcon(RoleManager.getRole("Peasant"), 1);
        addRoleIcon(RoleManager.getRole("Freeman"), 2);
        addRoleIcon(RoleManager.getRole("Knight"), 3);
        addRoleIcon(RoleManager.getRole("Noble"), 5);
        addRoleIcon(RoleManager.getRole("King"), 6);
        addRoleIcon(RoleManager.getRole("God"), 7);
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

    private void generateRoleInfo(ItemBuilder builder, Role role) {
        builder.addLoreMultiline("§9Effects");
        for (StatusEffectInstance effect : role.getEffects()) {
            String level = TextUtils.toRomanNumerals(effect.getAmplifier() + 1);
            String name = Text.translatable(effect.getTranslationKey()).getString();
            builder.addLoreLine("§7• " + name + " " + level);
        }
        if (!(role.name.equals("Freeman")||role.name.equals("Peasant"))) {
            builder.addLoreLine("§7+ Effects from previous roles");
        }

        List<ItemStack> rewards = role.getRankUpRewards();
        if (rewards.isEmpty()) return;

        builder.addLoreMultiline("\n§9Rewards");
        for (ItemStack itemStack : rewards) {
            String qtyText = itemStack.getCount() > 1 ? " §7x" +  itemStack.getCount() : "";
            String itemName = itemStack.getName().getString();
            itemName = TextUtils.removeFormatting(itemName);
            builder.addLoreLine("§7• " + itemName + qtyText);
        }
    }

    private void addRoleIcon(Role role, int slot) {
        Role playerRole = RoleManager.getPlayerRole(tPlayer);
        ItemBuilder roleIconBuilder = new ItemBuilder(role.itemIcon)
                .setName(role.titleFormat + "§l" + role.name)
                .removeAdditionalTooltips()
                .addLoreMultiline(role.description)
                .addLoreLine("");

        generateRoleInfo(roleIconBuilder, role);
        ItemStack roleIcon = roleIconBuilder.create();

        if (role.equals(playerRole)) {
            roleIcon.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        this.setItem(slot, roleIcon);

        Role nextRole = RoleManager.getNextRole(playerRole);
        // add glass pane indicator
        ItemBuilder paneBuilder = new ItemBuilder("minecraft:red_stained_glass_pane")
            .setName("§cProgress to " + role.titleFormat + "§l" + role.name);

        if (nextRole == null || RoleManager.getPreviousRoles(nextRole).contains(role)) { // already achieved
            paneBuilder.setItem("minecraft:lime_stained_glass_pane");
            paneBuilder.addLoreLine("§cYou have already reached this role!");
        }
        else if (role.equals(nextRole)) { // next role
            paneBuilder.setItem("minecraft:yellow_stained_glass_pane");
            int progressIntoRank = tPlayer.getProgressPoints() - playerRole.pointRequirement;
            int totalNeeded = role.pointRequirement - playerRole.pointRequirement;
            paneBuilder.addLoreMultiline("§e" + progressIntoRank + "§6/§e" + totalNeeded + " §aPP\n");
            generateRoleInfo(paneBuilder, role);
        }
        else { // future role
            paneBuilder.addLoreLine("§cReach the previous role first!");
        }

        setItem(slot + 9, paneBuilder.create());
    }

    private void addPlayerTasks() {
        Role role = RoleManager.getPlayerRole(tPlayer);
        Task currentTask = taskManager.getTaskFromPlayer(tPlayer);
        boolean playerHasTask = currentTask != null;

        List<TaskInfo> tasks = role.getPlayerTaskPool();
        for (int i = 0; i < RoleManager.PLAYER_TASKS_PER_ROLE; i++) {
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

                // add lore based on task state
                if (thisTaskSelected) { // task is selected - add progress bar and submit hint
                    taskIconBuilder.setItem("minecraft:filled_map");
                    taskIconBuilder.setEnchanted(true);
                    taskIconBuilder.addLoreMultiline("\n" + getProgressString(currentTask));

                    if (currentTask.getOptimisticPercentageProgress(player) >= 1.0f) {
                        taskIconBuilder.addLoreMultiline("\n§9Click to submit task");
                    } else if (currentTask instanceof CollectionTask collectionTask && collectionTask.getInventoryCount(player) > 0) {
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
                }
                else if (playerHasTask) { // another task has been selected
                    taskIconBuilder.addLoreMultiline("\n§c§oYou have already selected another task!");
                }
                else if (taskManager.isTaskCompleted(possibleTask)) { // this task is already completed
                    taskIconBuilder.addLoreMultiline("\n§c§oThis task has already been completed!");
                }
                else { // no task is selected
                    taskIconBuilder.addLoreMultiline("\n§9Click to select this task!");
                    // select event
                    clickAction = (player, slot, menuContext) -> {
                        String playerID = tPlayer.getPlayerId();
                        taskManager.addTask(possibleTask, playerID);
                        player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.UI, 1.0f, 1.0f);
                        refreshUI();
                    };
                }
            }
            else {
                taskIconBuilder.setName("Empty Task");
            }
            int slot = i < 7 ? 28 + i : 30 + i;
            setClickableItem(slot, taskIconBuilder.create(), clickAction);
        }
    }

    private void addGlobalTasks() {
        List<GlobalTask> tasks = RoleManager.getPlayerRole(tPlayer).getGlobalTasks();

        for (int i = 0; i < RoleManager.GLOBAL_TASKS_PER_ROLE; i++) {
            ItemBuilder taskIconBuilder = new ItemBuilder("minecraft:painting");
            MenuClickAction clickAction = MenuClickAction.NO_ACTION;
            if (i < tasks.size()) {
                GlobalTask task = tasks.get(i);

                // add title
                taskIconBuilder.setName(task.getFormattedName() + " §7(§d§lGLOBAL§r§d\uD83C\uDF0E§7)");
                // add pp reward
                taskIconBuilder.addLoreLine("§7Grants §e" + task.progressPointReward + " §aPP" + "§7 across all contributors");
                // add progress bar
                taskIconBuilder.addLoreMultiline("\n" + getProgressString(task));

                boolean completed = taskManager.completedGlobalTasks.contains(task.name);
                if (!completed) {
                    taskIconBuilder.setEnchanted(true);
                    taskIconBuilder.addLoreMultiline("\n§7You have contributed §e" + task.getPlayerContribution(tPlayer.getPlayerId()) + " §7" + task.getTargetDisplayName());
                    taskIconBuilder.addLoreLine("§7You will receive §e" + task.getPlayerContributionAsProgressPoints(tPlayer.getPlayerId()) + " §aPP §7upon completion");

                    if (task instanceof GlobalCollectionTask) {
                        taskIconBuilder.addLoreMultiline("\n§9Click to contribute");

                        clickAction = (player, slot, menuContext) -> {
                            SubmitStatus status = taskManager.submitTask(task.name, (ServerPlayerEntity) player);
                            if (status == SubmitStatus.PARTIAL || status == SubmitStatus.COMPLETED) {
                                player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.UI, 1.0f, 1.0f);
                                refreshUI();
                            } else {
                                player.playSoundToPlayer(SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.UI, 1.0f, 1.0f);
                            }
                        };
                    }
                }
                else {
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
        String result = "§e" + task.progress + "§6/§e" + task.maxProgress + " §7" + task.getTargetDisplayName() + " " + TaskType.pastVerb(TaskType.get(task)) + "\n";
        result += TextUtils.progressBarWithOptimisticProgress(16, task.getPercentageProgress(), true, task.getOptimisticPercentageProgress(player));
        return result;
    }

    private void addCancelButton() {
        if (taskManager.getTaskFromPlayer(tPlayer) == null) return;
        ItemBuilder cancelBuilder = new ItemBuilder("minecraft:red_stained_glass")
                .setName("§c§lCANCEL TASK")
                .addLoreMultiline("§7Clicking this will cancel the current task\n\n§7Your current progress towards the task §nwill not be saved");

        setClickableItem(45, cancelBuilder.create(), (player, slot, menuContext) -> {
            // first click action brings up confirm button
            player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.UI, 1.0f, 0.5f);

            ItemBuilder confirmBuilder = new ItemBuilder("minecraft:red_concrete").setName("§c§lCLICK AGAIN TO CONFIRM");
            setClickableItem(45, confirmBuilder.create(), (player2, slot2, menuContext2) -> {
                // on confirm click, cancel the task
                taskManager.cancelPlayerTask(player.getUuidAsString());
                player.playSoundToPlayer(SoundEvents.ITEM_TOTEM_USE, SoundCategory.UI, 1.0f, 1.0f);
                refreshUI();
            });
        });
    }
}
