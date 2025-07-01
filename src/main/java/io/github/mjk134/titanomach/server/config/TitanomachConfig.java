package io.github.mjk134.titanomach.server.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.Property;
import io.github.mjk134.titanomach.server.TitanomachPlayer;
import io.github.mjk134.titanomach.server.tasks.CollectionTask;
import io.github.mjk134.titanomach.server.tasks.SlayerTask;
import io.github.mjk134.titanomach.server.tasks.Task;
import io.github.mjk134.titanomach.server.tasks.TaskManager;
import io.github.mjk134.titanomach.utils.RuntimeTypeAdapterFactory;
import io.github.mjk134.titanomach.utils.Skin;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static io.github.mjk134.titanomach.Titanomach.*;

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
    private HashMap<String, TitanomachPlayer> playerConfigs = new HashMap<>();
    // Add skin pool here - make it a map for the skin id
    private HashMap<String, Skin> skinPool = new HashMap<String, Skin>();
    private static final RuntimeTypeAdapterFactory<Task> taskAdapterFactory = RuntimeTypeAdapterFactory
            .of(Task.class, "type")
            .registerSubtype(CollectionTask.class, "collection")
            .registerSubtype(SlayerTask.class, "slayer");
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapterFactory(taskAdapterFactory)
            .create();

    private Date lastSessionDate;
    private static final Date initialisedCurrentDate = new Date();
    private boolean hasVoteOccurred = false;
    private boolean fivePlayersActive = false;
    private TaskManager taskManager =  new TaskManager();

    public TitanomachConfig() {}

    public void dump() {
        try {
            FileWriter writer = new FileWriter(MOD_ID + ".json");
            String json = GSON.toJson(this);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            MOD_LOGGER.error("An error occurred when dumping the Titanomach config.");
            MOD_LOGGER.error(e.getMessage());
        }
    }

    public void initialize() {
        // TODO: CHANGE THIS TO SUPPORT SERVER CRASHES
        // Do some presession configuration
        if (this.lastSessionDate == null) {
            this.lastSessionDate = new Date();
            this.dump();
            return;
        }
        if (this.lastSessionDate.before(new Date())) {
            this.lastSessionDate = new Date();
            if (!hasVoteOccurred && fivePlayersActive) {
                // do no vote penalty on all players
            }
            // Loop over players and reduce their PP session duration
            for (TitanomachPlayer player : playerConfigs.values()) {
                if (player.multiplierDuration > 0) {
                    // Reduce duration
                    player.multiplierDuration = player.multiplierDuration - 1;
                    if (player.multiplierDuration == 0) {
                        if (!player.isHostile()) {
                            // Reset multiplier
                            player.progressPointMultiplier = 1;
                        }
                    }
                }
            }
        }
        this.dump();
    }

    public static TitanomachConfig load() {
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
            TitanomachConfig config = GSON.fromJson(reader, TitanomachConfig.class);
            reader.close();
            config.initialize();
            MOD_LOGGER.info("Loaded Titanomach config.");
            return config;
        } catch (IOException e) {
            MOD_LOGGER.error("Failed to load config file, the class is replaced.");
            MOD_LOGGER.error(e.getMessage());
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

    public TitanomachPlayer getPlayerConfig(ServerPlayerEntity player) {
        return getPlayerConfig(player.getUuidAsString());
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
        return new Property("textures", skin.getTexture(), skin.getSignature());
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.dump();
    }

    public Boolean isStarted() {
        return started;
    }

    /**
     * Call this method to start the titanomachy.
     */
    public void start() {
        this.started = true;
        // Get keys of skins
        List<String> keys = this.skinPool.keySet().stream().toList();
        int keysSize = keys.size();
        if (keysSize > 0) {
            for (TitanomachPlayer player : this.playerConfigs.values()) {
                // set skin id to blank
                player.getRandomIdentity().setSkinId("");
                MOD_LOGGER.info("Set {} to {}", player.getPlayerId(), player.getRandomIdentity().getSkinId());
            }

            MOD_LOGGER.info("Beginning to set the skins.");
            for (TitanomachPlayer player : this.playerConfigs.values()) {

                int randomNum = (int)(Math.random() * keysSize);
                String id = keys.get(randomNum);

                while (true) {
                    boolean isInUse = false;
                    for (TitanomachPlayer checkedPlayer : this.playerConfigs.values()) {
                        if (checkedPlayer.getRandomIdentity().getSkinId().equals(id)) {

                            isInUse = true;
                            break;
                        }
                    }
                    MOD_LOGGER.info("Random number set to {}", randomNum);
                    if (isInUse) {
                        randomNum = (int)(Math.random() * keysSize);
                        id = keys.get(randomNum);
                        MOD_LOGGER.info("Skin in use attempt using for player {}: {}", player.getPlayerId(), id);
                    } else {
                        MOD_LOGGER.info("Skin not in use using for player {}: {}", player.getPlayerId(), id);
                        break;
                    }
                }

                player.getRandomIdentity().setSkinId(id);
            }
        }
        this.dump();
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

}
