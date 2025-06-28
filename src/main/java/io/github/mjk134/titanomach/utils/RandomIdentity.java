package io.github.mjk134.titanomach.utils;

import com.mojang.authlib.properties.Property;
import net.minecraft.server.network.ServerPlayerEntity;

public class RandomIdentity {
    private String skinId;
    private String playerName;

    public RandomIdentity(String skinId, String playerName) {
        this.skinId = skinId;
        this.playerName = playerName;
    }

    public static RandomIdentity getRandomIdentity(ServerPlayerEntity player) {
        int randomNum = (int)(Math.random() * 101);
        return new RandomIdentity(player.getUuidAsString(), "Monkey" + randomNum);
    }

    public String getSkinId() {
        return skinId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setSkinId(String skinId) {
        this.skinId = skinId;
    }

}
