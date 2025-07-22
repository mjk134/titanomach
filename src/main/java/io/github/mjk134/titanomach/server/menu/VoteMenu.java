package io.github.mjk134.titanomach.server.menu;

import com.mojang.authlib.GameProfile;
import io.github.mjk134.titanomach.Titanomach;
import io.github.mjk134.titanomach.server.TitanomachPlayer;
import io.github.mjk134.titanomach.server.roles.Role;
import io.github.mjk134.titanomach.server.roles.RoleManager;
import io.github.mjk134.titanomach.utils.ItemBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
            ItemVotingMenu menu = new ItemVotingMenu(player);
            menu.displayTo(player);
        });


    }


    class PlayerVotingMenu extends Menu {

        public PlayerVotingMenu(ServerPlayerEntity player) {
            super("Vote for a player");
            fillEmptyWithGlass();
        }

        private ItemStack getPlayerHead(ServerPlayerEntity player) {
            TitanomachPlayer tPlayer = Titanomach.TITANOMACH_CONFIG.getPlayerConfig((ServerPlayerEntity) player);
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

    class ItemVotingMenu extends Menu {

        public ItemVotingMenu(ServerPlayerEntity player) {
            super("Sacrifice an item");
            fillEmptyWithGlass();
        }
    }
}
