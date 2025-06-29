package io.github.mjk134.titanomach.utils;

import net.minecraft.server.network.ServerPlayerEntity;

public class Identity {
    private String skinId;
    private String playerName;

    public Identity(String skinId, String playerName) {
        this.skinId = skinId;
        this.playerName = playerName;
    }

    public static Identity getRandomIdentity(ServerPlayerEntity player) {
        int randomNum = (int)(Math.random() * 101);
        return new Identity(player.getUuidAsString(), "Monkey" + randomNum);
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
