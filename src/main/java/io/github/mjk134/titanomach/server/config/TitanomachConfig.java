package io.github.mjk134.titanomach.server.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.Property;
import io.github.mjk134.titanomach.server.TitanomachPlayer;
import io.github.mjk134.titanomach.utils.Skin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.github.mjk134.titanomach.Titanomach.MOD_ID;
import static io.github.mjk134.titanomach.Titanomach.ModLogger;

/**
 * This is used to update the data in the server about the player so that state can be fetched when server is brought backup.
 */
public class TitanomachConfig {
    /**
     * This field indicates whether the mod carries out its actions, is only modified by the /start command (and potentially a /pause)
     */
    private Boolean enabled = false;
    private Boolean started = false;
    /**
     * Stores all the data about the players. UUID, Data
     */
    private HashMap<String, TitanomachPlayer> playerConfigs = new HashMap<String, TitanomachPlayer>();
    // Add skin pool here - make it a map for the skin id
    private HashMap<String, Skin> skinPool = new HashMap<String, Skin>();

    public TitanomachConfig() {}

    public void dump() {
        try {
            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
            FileWriter writer = new FileWriter(MOD_ID + ".json");
            String json = gson.toJson(this);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            ModLogger.error("An error occurred when dumping the Titanomach config.");
            ModLogger.error(e.getMessage());
        }
    }

    public static TitanomachConfig load() {
        Gson gson = new Gson();
        // Check if the file exists
        File file = new File(MOD_ID + ".json");
        if (!file.exists()) {
            TitanomachConfig config = new TitanomachConfig();
            config.dump();
            return config;
        }
        // Load the file if it does exist
        // Do try, catch in here to avoid static blocks in the main class
        try {
            FileReader reader = new FileReader(MOD_ID + ".json");
            TitanomachConfig config = gson.fromJson(reader, TitanomachConfig.class);
            reader.close();
            ModLogger.info("Loaded Titanomach config.");
            return config;
        } catch (IOException e) {
            ModLogger.error("Failed to load config file, the class is replaced.");
            ModLogger.error(e.getMessage());
            return new TitanomachConfig();
        }
    }

    public void addPlayerConfig(String playerId, TitanomachPlayer player) {
        playerConfigs.put(playerId, player);
        this.dump();
    }

    public TitanomachPlayer getPlayerConfig(String playerId) {
        return playerConfigs.get(playerId);
    }

    public Boolean isPlayerInConfig(String playerId) {
        return playerConfigs.containsKey(playerId);
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void addToSkinPool(Skin skin) {
        skinPool.put(skin.getUUID(), skin);
        this.dump();
    }

    public Skin getSkin(String uuid) {
        return skinPool.get(uuid);
    }

    public Property getSkinProperty(String uuid) {
        Skin skin = skinPool.get(uuid);
        if (skin == null) {
            return null;
        }
        return new Property("textures", skin.getTexture(), skin.getTexture());
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.dump();
    }

    public Boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
        this.dump();
    }


}
