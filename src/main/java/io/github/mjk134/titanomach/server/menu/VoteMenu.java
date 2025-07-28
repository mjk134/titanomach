package io.github.mjk134.titanomach.server.menu;

import com.mojang.authlib.GameProfile;
import io.github.mjk134.titanomach.Titanomach;
import io.github.mjk134.titanomach.server.TitanomachPlayer;
import io.github.mjk134.titanomach.server.roles.Role;
import io.github.mjk134.titanomach.server.roles.RoleManager;
import io.github.mjk134.titanomach.server.vote.VoteManager;
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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.mjk134.titanomach.Titanomach.MOD_LOGGER;
import static io.github.mjk134.titanomach.Titanomach.TITANOMACH_CONFIG;

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
                .addLoreLine("")
                .addLoreLine("This selects the item in your main hand")
                .addLoreLine("as the sacrifice item.")
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
            TitanomachPlayer tPlayer = Titanomach.TITANOMACH_CONFIG.getPlayerConfig(player);
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
                    (_player, _slot, _ctx) -> {

                    });

            setClickableItem(24, new ItemBuilder("minecraft:red_wool").create(), (_player, _slot, _ctx) -> {

            });

            setInventoryAction((_player, _slot, _ctx) -> {
                Inventory inv = this.getInventory();
                ItemStack clickedStack = _player.getInventory().getStack(_slot);
                if (inv.getStack(ITEM_SLOT).isEmpty() && !clickedStack.isEmpty()) {
                    setItem(ITEM_SLOT, clickedStack.copyWithCount(1));
                    clickedStack.decrement(1);
                    _player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.UI, 1.0f, 1.0f);
                }
            });

            setItem(40, new ItemBuilder("minecraft:filled_map").create());

            fillEmptyWithGlass();
            setClickableItem(ITEM_SLOT, ItemStack.EMPTY, (_player, _slot, _ctx) -> {
                Inventory inv = this.getInventory();
                if (!inv.getStack(ITEM_SLOT).isEmpty()) {
                    _ctx.quickMove(_player, _slot);
                    _player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.UI, 1.0f, 0.1f);
                }
            });
        }
        public void onClose(PlayerEntity player, MenuScreenHandler ctx) {
            Inventory inv = this.getInventory();
            if (!inv.getStack(ITEM_SLOT).isEmpty()) {
                // setting the stack ensures the item will either end up in the player's inv or on the ground
                ctx.setCursorStack(inv.getStack(ITEM_SLOT));
            }
        }
    }

}
