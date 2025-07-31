package io.github.mjk134.titanomach.server.menu;

import com.mojang.authlib.GameProfile;
import io.github.mjk134.titanomach.Titanomach;
import io.github.mjk134.titanomach.server.TitanomachPlayer;
import io.github.mjk134.titanomach.server.roles.Role;
import io.github.mjk134.titanomach.server.roles.RoleManager;
import io.github.mjk134.titanomach.server.sacrifice.VoteManager;
import io.github.mjk134.titanomach.utils.ItemBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;

import java.util.List;

public class VoteMenu extends Menu {
    public VoteMenu(ServerPlayerEntity player) {
        super("Produce a sacrifice");
        ItemStack blueWool = new ItemBuilder("minecraft:light_blue_wool")
                .addLoreLine(Text.of("§r§eVoting for a player makes them"))
                .addLoreLine(Text.of("§r§ea candidate for the sacrifice."))
                .addLoreLine("")
                .addLoreLine(Text.literal("§rIt is also §r§oanonymous §rso you don't"))
                .addLoreLine(Text.of("§rmake enemies through this process."))
                .setName(Text.literal("Vote for a player")
                        .formatted(Formatting.BLUE, Formatting.BOLD))
                .create();

        setClickableItem(20, blueWool, (_player, _slot, menuContext) -> {
            PlayerVotingMenu menu = new PlayerVotingMenu(player);
            menu.displayTo(player);
        });

        ItemStack pinkWool = new ItemBuilder("minecraft:pink_wool")
                .addLoreLine(Text.of("§r§eYou can sacrifice an item to become"))
                .addLoreLine(Text.of("§r§eexempt from the vote entirely."))
                .setName(Text.literal("Sacrifice an ")
                            .formatted(Formatting.GOLD, Formatting.BOLD)
                            .append(Text.literal("EPIC").formatted(Formatting.DARK_PURPLE, Formatting.BOLD))
                                .append(Text.literal(" item")
                                        .formatted(Formatting.GOLD, Formatting.BOLD))
                        )
                .create();

        setClickableItem(24, pinkWool, (_player, _slot, menuContext) -> {
            ConfirmItemSacrificeMenu menu = new ConfirmItemSacrificeMenu();
            menu.displayTo(player);
        });
    }

    static class PlayerVotingMenu extends Menu {
        public PlayerVotingMenu(ServerPlayerEntity player) {
            super("Vote for a player");
            MinecraftServer server = player.getServer();
            assert server != null;
            List<ServerPlayerEntity> playersToVoteFor = server.getPlayerManager().getPlayerList();
            int playerIndex = 0;

            for (int row = 1; row <= 4; row++) {
                for (int col = 1; col <= 7; col++) {
                    if (playerIndex >= playersToVoteFor.size()) {
                        break;
                    }

                    // 9 being total num cols
                    int slot = row * 9 + col;

                    ServerPlayerEntity votedForPlayer = playersToVoteFor.get(playerIndex);
                    setClickableItem(slot, getPlayerHead(votedForPlayer), (_player, _s, context) -> {
                        VoteManager.putVote(player, votedForPlayer);
                        player.closeHandledScreen();
                    });
                    playerIndex++;
                }
                if (playerIndex >= playersToVoteFor.size()) {
                    break;
                }
            }

            fillEmptyWithGlass();
        }

        private ItemStack getPlayerHead(ServerPlayerEntity player) {
            TitanomachPlayer tPlayer = Titanomach.CONFIG.getPlayerConfig(player);
            Role playerRole = RoleManager.getPlayerRole(tPlayer);
            GameProfile gameProfile = player.getGameProfile();

            ItemBuilder playerHeadBuilder = new ItemBuilder("minecraft:player_head")
                    .setName("§d" + gameProfile.getName())
                    .addLoreLine("§7Rank: " + playerRole.titleFormat + "§l" + playerRole.name);

            ItemStack playerHead = playerHeadBuilder.create();
            playerHead.set(DataComponentTypes.PROFILE, new ProfileComponent(gameProfile));
            return playerHead;
        }
    }

    static class ConfirmItemSacrificeMenu extends Menu {
        public static final int ITEM_SLOT = 22;

        public ConfirmItemSacrificeMenu() {
            super("Confirm item sacrifice");

            setNonEpicSubmitButton();

            setInventoryAction((player, slot, _ctx) -> {
                Inventory inv = this.getInventory();
                ItemStack clickedStack = player.getInventory().getStack(slot);
                if (inv.getStack(ITEM_SLOT).isEmpty() && !clickedStack.isEmpty()) {
                    setItem(ITEM_SLOT, clickedStack.copyWithCount(1));
                    clickedStack.decrement(1);
                    player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.UI, 1.0f, 1.0f);
                    if (this.isSelectedEpic()) {
                        setEpicSubmitButton();
                    } else {
                        setNonEpicSubmitButton();
                    }
                }
            });

            setClickableItem(24, new ItemBuilder("minecraft:red_wool")
                    .addLoreLine(Text.literal("If you don't submit an item").formatted(Formatting.YELLOW))
                    .addLoreLine(Text.literal("you won't be exempt from the vote.").formatted(Formatting.YELLOW))
                    .setName(Text.literal("CANCEL").formatted(Formatting.BOLD, Formatting.RED))
                    .create(),
                    (player, _slot, _ctx) -> ((ServerPlayerEntity) player).closeHandledScreen()
            );

            setItem(40, new ItemBuilder("minecraft:filled_map")
                    .addLoreLine("Click on an item to move it to the selected slot!")
                    .create());

            fillEmptyWithGlass();

            setClickableItem(ITEM_SLOT, ItemStack.EMPTY, (player, slot, ctx) -> {
                Inventory inv = this.getInventory();
                if (!inv.getStack(ITEM_SLOT).isEmpty()) {
                    ctx.quickMove(player, slot);
                    player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.UI, 1.0f, 0.1f);
                    setNonEpicSubmitButton();
                }
            });
        }

        private void setEpicSubmitButton() {
            setClickableItem(20,
                    new ItemBuilder("minecraft:green_wool")
                            .addLoreLine(
                                    Text.literal("Submitting this item gets rid of it")
                                            .formatted(Formatting.YELLOW)
                                            .append(Text.literal(" permanently.")
                                                    .formatted(Formatting.BOLD, Formatting.YELLOW)
                                            )
                            )
                            .setName(Text.literal("SUBMIT")
                                    .formatted(Formatting.GREEN, Formatting.BOLD))
                            .create(),
                    (player, _s, ctx) -> {
                        if (this.isSelectedEpic()) {
                            VoteManager.sacrificedItemPlayers.add(player.getUuid());
                            player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.UI, 1.0f, 0.1f);
                            setItem(ITEM_SLOT, ItemStack.EMPTY);
                            ((ServerPlayerEntity) player).closeHandledScreen();
                        }
                    });
        }

        private void setNonEpicSubmitButton() {
            setItem(20, new ItemBuilder("minecraft:green_wool")
                    .addLoreLine(
                            Text.literal("The selected item is not")
                                    .formatted(Formatting.YELLOW)
                                    .append(Text.literal(" EPIC.")
                                            .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC, Formatting.BOLD)
                                    )
                    )
                    .setName(Text.literal("SUBMIT")
                            .formatted(Formatting.DARK_RED, Formatting.BOLD))
                    .create());
        }

        public void onClose(PlayerEntity player, MenuScreenHandler ctx) {
            Inventory inv = this.getInventory();
            if (!inv.getStack(ITEM_SLOT).isEmpty()) {
                // setting the stack ensures the item will either end up in the player's inv or on the ground
                ctx.setCursorStack(inv.getStack(ITEM_SLOT));
            }
        }

        public boolean isSelectedEpic() {
            return this.getInventory().getStack(ITEM_SLOT).getRarity() == Rarity.EPIC;
        }
    }

}
