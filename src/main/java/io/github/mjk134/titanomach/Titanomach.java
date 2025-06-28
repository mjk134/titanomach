package io.github.mjk134.titanomach;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.github.mjk134.titanomach.server.TitanomachPlayer;
import io.github.mjk134.titanomach.server.commands.CommandHandler;
import io.github.mjk134.titanomach.server.config.TitanomachConfig;
import io.github.mjk134.titanomach.server.entity.ServerTitanomachPlayer;
import io.github.mjk134.titanomach.server.menu.MenuManager;
import io.github.mjk134.titanomach.utils.Skin;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Titanomach implements ModInitializer {

    public static final String MOD_ID = "titanomach";
    public static final Logger ModLogger = LoggerFactory.getLogger(MOD_ID);
    public static final TitanomachConfig TITANOMACH_CONFIG = TitanomachConfig.load();
    public static final ExecutorService THREADPOOL = Executors.newCachedThreadPool();

    @Override
    public void onInitialize() {
        ModLogger.info(MOD_ID + " has initialized.");

        ServerPlayConnectionEvents.INIT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();

            THREADPOOL.submit(() -> {
                Optional<String> value = ((ServerTitanomachPlayer) player).titanomach$getSkinValue();
                Optional<String> signature = ((ServerTitanomachPlayer) player).titanomach$getSkinSignature();

                ModLogger.info("Test skins {} {}", value, signature);

                Property property = TITANOMACH_CONFIG.getSkinProperty(TITANOMACH_CONFIG.getPlayerConfig(player.getUuidAsString()).getRandomIdentity().getSkinId());
                ((ServerTitanomachPlayer) player).titanomach$setSkin(property, true);
            });
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            player.sendMessage(Text.literal("Welcome to Titanomach!"), true);
            Boolean isInConfig = TITANOMACH_CONFIG.isPlayerInConfig(player.getUuidAsString());
            // Initialise player here
            if (!isInConfig) {
                TITANOMACH_CONFIG.addPlayerConfig(player.getUuidAsString(), new TitanomachPlayer(player));
                PropertyMap propertyMap = player.getGameProfile().getProperties();
                Property skinProperty = (Property) propertyMap.get("textures").toArray()[0];
                if (skinProperty.hasSignature()) TITANOMACH_CONFIG.addToSkinPool(new Skin(skinProperty.value(), skinProperty.signature(), player.getUuidAsString()));
            }
            //Property property = TITANOMACH_CONFIG.getSkinProperty(TITANOMACH_CONFIG.getPlayerConfig(player.getUuidAsString()).getRandomIdentity().getSkinId());
            //((ServerTitanomachPlayer) player).titanomach$setSkin(property, true);
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, registrationEnvironment) -> {
            ModLogger.info("Command dispatcher has been initialized!");
            CommandHandler.registerCommands(dispatcher);
        });

        

        // Probably need to move this somewhere else
        MenuManager.createMenus();
    }


}
