package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.Titanomach;
import io.github.mjk134.titanomach.server.TitanomachPlayer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class RoleManager {
    public final static int EFFECT_INTERVAL_TICKS = 100;
    public final static String DEFAULT_ROLE_NAME = "Peasant";
    private static int effectIntervalCounter = 0;
    private static final HashMap<String, Role> roles = new HashMap<>();
    private static final ArrayList<Role> rolesOrdered = new ArrayList<>();

    public static void initialise() {
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            effectIntervalCounter++;
            if (effectIntervalCounter == EFFECT_INTERVAL_TICKS) {
                applyRoleEffects(server);
                effectIntervalCounter = 0;
            }
        });

        addRole(new PeasantRole());
        addRole(new FreemanRole());
        addRole(new KnightRole());
        addRole(new NobleRole());
        addRole(new KingRole());
        addRole(new GodRole());
    }

    private static void applyRoleEffects(MinecraftServer server) {
        // iterate through each online player
        server.getPlayerManager().getPlayerList().forEach(player -> {
            String uuid = player.getUuidAsString();
            TitanomachPlayer titanomachPlayer = Titanomach.TITANOMACH_CONFIG.getPlayerConfig(uuid);

            Role role = getPlayerRole(titanomachPlayer);
            if (role != null)
                role.onEffectTick(player);
        });
    }

    private static void addRole(Role role) {
        roles.put(role.name, role);
        rolesOrdered.add(role);
    }

    /// Get a Role from its string name
    public static Role getRole(String roleName) {
        return roles.get(roleName);
    }

    public static Role getPlayerRole(TitanomachPlayer player) {
        return calculateRole(player.getProgressPoints());
    }

    /// Get the next role in progression, returns null if max rank
    public static Role getNextRole(Role role) {
        int nextIdx = rolesOrdered.indexOf(role) + 1;
        if (nextIdx >= rolesOrdered.size())
            return null;
        return rolesOrdered.get(nextIdx);
    }

    public static float getPercentageProgressToNextRole(TitanomachPlayer player) {
        Role playerRole = getPlayerRole(player);
        Role nextRole = RoleManager.getNextRole(playerRole);
        if (nextRole != null) {
            int totalPointsNeeded = nextRole.pointRequirement - playerRole.pointRequirement;
            int playerProgress = player.getProgressPoints() - playerRole.pointRequirement;
            return (float)playerProgress / (float)totalPointsNeeded;
        }
        return 1.0f;
    }

    public static ArrayList<Role> getAllRoles() {
        return rolesOrdered;
    }

    public static Role calculateRole(int points) {
        for (int i = 0; i < rolesOrdered.size(); i++) {
            Role role = rolesOrdered.get(i);
            if (role.pointRequirement > points) {
                return rolesOrdered.get(Integer.max(0, i - 1));
            }
        }
        return rolesOrdered.getLast();
    }
}
