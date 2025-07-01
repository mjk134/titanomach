package io.github.mjk134.titanomach.utils;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Identity {
    private String skinId;
    private String playerName;
    private static final List<String> MEDIEVAL_MALE_NAMES = Arrays.asList(
            "Alan", "Arthur", "Bartholomew", "Benedict", "Edmund", "Edward",
            "Geoffrey", "Gilbert", "Henry", "Hugh", "John", "Nicholas", "Peter",
            "Philip", "Ralph", "Reginald", "Richard", "Robert", "Roger", "Simon",
            "Stephen", "Thomas", "Walter", "William", "Adelard", "Aldous",
            "Anselm", "Arnold", "Drogo", "Everard", "Fulke", "Gerard", "Godfrey",
            "Guy", "Herbert", "Lambert", "Lionel", "Martin", "Milo",
            "Osbert", "Reynold", "Theobald", "Warin"
    );

    // A single Random instance is also more efficient than calling Math.random() repeatedly.
    private static final Random random = new Random();

    public Identity(String skinId, String playerName) {
        this.skinId = skinId;
        this.playerName = playerName;
    }

    public static Identity getRandomIdentity(ServerPlayerEntity player) {
        int randomIndex = random.nextInt(MEDIEVAL_MALE_NAMES.size());
        String randomName = MEDIEVAL_MALE_NAMES.get(randomIndex);
        return new Identity(player.getUuidAsString(),  randomName);
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
