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
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.mjk134.titanomach.Titanomach.MOD_LOGGER;

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
            // select item in hand
            MOD_LOGGER.info(player.getMainHandStack().getName().getString());

            ConfirmItemSacrificeMenu menu = new ConfirmItemSacrificeMenu(player.getMainHandStack());
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
        public ConfirmItemSacrificeMenu(ItemStack itemStack) {
            super("Confirm item sacrifice");

            setItem(22, itemStack);

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

            setInventoryAction((_p, _s, _mctx) -> {

            });

            setItem(40, new ItemBuilder("minecraft:filled_map").create());

            fillEmptyWithGlass();
        }
    }

}
