package io.github.mjk134.titanomach;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.github.mjk134.titanomach.server.TitanomachPlayer;
import io.github.mjk134.titanomach.server.commands.CommandHandler;
import io.github.mjk134.titanomach.server.config.TitanomachConfig;
import io.github.mjk134.titanomach.server.entity.ServerTitanomachPlayer;
import io.github.mjk134.titanomach.server.roles.RoleManager;
import io.github.mjk134.titanomach.server.sacrifice.SacrificeManager;
import io.github.mjk134.titanomach.server.tasks.TaskManager;
import io.github.mjk134.titanomach.server.sacrifice.VoteManager;
import io.github.mjk134.titanomach.utils.Skin;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Titanomach implements ModInitializer {

    public static final String MOD_ID = "titanomach";
    public static final Logger MOD_LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final TitanomachConfig CONFIG = TitanomachConfig.load();
    public static final ExecutorService THREADPOOL = Executors.newCachedThreadPool();
    public static MinecraftServer SERVER_INSTANCE;

    @Override
    public void onInitialize() {
        MOD_LOGGER.info(MOD_ID + " is initializing.");

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            VoteManager.resumeStopwatch(player);
            player.sendMessage(Text.literal("Welcome to Titanomach!"), true);
            Boolean isInConfig = CONFIG.isPlayerInConfig(player.getUuidAsString());
            // Initialise player here
            if (!isInConfig) {
                CONFIG.addPlayerConfig(player.getUuidAsString(), new TitanomachPlayer(player));
                PropertyMap propertyMap = player.getGameProfile().getProperties();
                // Probably always a property IDK
                Property skinProperty = (Property) propertyMap.get("textures").toArray()[0];
                if (skinProperty.hasSignature()) CONFIG.addToSkinPool(new Skin(skinProperty.value(), skinProperty.signature(), player.getUuidAsString()));
            }

            if (CONFIG.isStarted()) {
                TitanomachPlayer titanomachPlayer = CONFIG.getPlayerConfig(player);
                if (!titanomachPlayer.getHasJoined()) {
                    // TODO: IMPLEMENT JOIN LOGIC I.E. BROADCAST MESSAGES ADD BOOK AND QUILL ETC
                    titanomachPlayer.setHasJoined(true);
                    CONFIG.dump();
                }

                THREADPOOL.submit(() -> {
                    // TODO: REFACTOR THIS TO MAKE IT CLEANER
                    Property property = CONFIG.getSkinProperty(CONFIG.getPlayerConfig(player).getRandomIdentity().getSkinId());
                    ((ServerTitanomachPlayer) player).titanomach$setSkin(property, true);
                });

                SacrificeManager.reduceHearts(player);
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            VoteManager.stopStopwatch(player);
        }));


        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((handler, sender, entity) -> {
            if (sender instanceof ServerPlayerEntity player) {
                TaskManager taskManager = CONFIG.getTaskManager();
                taskManager.handleEntityKill(player, entity);

                if (entity instanceof ServerPlayerEntity victim) {
                    // entity/player is killed by sender so we have to mark them as hostile
                    // check if notoriety level is 0
                    TitanomachPlayer titanomachPlayer = CONFIG.getPlayerConfig(player);
                    TitanomachPlayer victimPlayer = CONFIG.getPlayerConfig(victim);
                    int murdererLevel = titanomachPlayer.getNotorietyLevel();
                    int victimLevel = victimPlayer.getNotorietyLevel();
                    PlayerManager playerManager = handler.getServer().getPlayerManager();

                    if (murdererLevel == 0 && victimLevel == 0) {
                        // send message to all players
                        for (ServerPlayerEntity serverPlayer : playerManager.getPlayerList()) {
                            serverPlayer.sendMessage(Text.literal(player.getStyledDisplayName().getString() + " is now §c§l HOSTILE!"));
                        }
                        titanomachPlayer.incrementNotorietyLevel();
                        titanomachPlayer.progressPointMultiplier += 1;
                    } else if (victimLevel != 0 && murdererLevel == 0) {
                        // Declare murderer as mercenary -> gain 3x rewards for the next 2 sessions
                        victimPlayer.resetNotorietyLevel();
                        titanomachPlayer.progressPointMultiplier += 3;
                        titanomachPlayer.multiplierDuration = 2;
                        for (ServerPlayerEntity serverPlayer : playerManager.getPlayerList()) {
                            serverPlayer.sendMessage(Text.literal(player.getStyledDisplayName().getString() + " is now §c§l MERCENARY!"));
                        }
                    } else if (victimLevel != 0) {
                        // A hostile killed another hostile -> neutralise the victim but no mercenary effect is awarded
                        victimPlayer.resetNotorietyLevel();
                        // Give the hostile a further boost in PP so add 1 to the multiplier
                        titanomachPlayer.incrementNotorietyLevel();
                        titanomachPlayer.progressPointMultiplier = titanomachPlayer.progressPointMultiplier + 1;
                    } else {
                        // Hostile kills a victim, increase the hostile's multiplier
                        titanomachPlayer.incrementNotorietyLevel();
                        titanomachPlayer.progressPointMultiplier = titanomachPlayer.progressPointMultiplier + 1;
                    }
                    CONFIG.dump();
                }
            }
        });

        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            if (CONFIG.isEnabled()) {
                CONFIG.getTaskManager().tick(server);
                RoleManager.tick(server);
                SacrificeManager.applyDebuffsOnTick(server);
            }
        });

        ServerLifecycleEvents.SERVER_STARTING.register((server) -> SERVER_INSTANCE = server);

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, registrationEnvironment) -> {
            MOD_LOGGER.info("Command dispatcher has been initialized!");
            CommandHandler.registerCommands(dispatcher);
        });

        RoleManager.initialise();

        MOD_LOGGER.info(MOD_ID + " has initialised.");
    }


}
