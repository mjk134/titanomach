package io.github.mjk134.titanomach.server.roles;

import io.github.mjk134.titanomach.Titanomach;
import io.github.mjk134.titanomach.server.TitanomachPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoleManager {
    public final static int EFFECT_INTERVAL_TICKS = 100;
    public final static String DEFAULT_ROLE_NAME = "Peasant";
    public final static int PLAYER_TASKS_PER_ROLE = 14;
    public final static int GLOBAL_TASKS_PER_ROLE = 3;
    private static int effectIntervalCounter = 0;
    private static final HashMap<String, Role> roles = new HashMap<>();
    private static final ArrayList<Role> rolesOrdered = new ArrayList<>();

    public static void initialise() {
        addRole(new PeasantRole());
        addRole(new FreemanRole());
        addRole(new KnightRole());
        addRole(new NobleRole());
        addRole(new KingRole());
        addRole(new GodRole());
    }

    public static void tick(MinecraftServer server) {
        effectIntervalCounter++;
        if (effectIntervalCounter == EFFECT_INTERVAL_TICKS) {
            applyRoleEffects(server);
            effectIntervalCounter = 0;
        }
    }

    private static void applyRoleEffects(MinecraftServer server) {
        // iterate through each online player
        server.getPlayerManager().getPlayerList().forEach(player -> {
            String uuid = player.getUuidAsString();
            TitanomachPlayer titanomachPlayer = Titanomach.TITANOMACH_CONFIG.getPlayerConfig(uuid);

            Role role = getPlayerRole(titanomachPlayer);
            if (role != null) {
                // Each previous role effect is also added
                for (Role activeRole : getPreviousRoles(role)) {
                    activeRole.onEffectTick(player);
                }
                role.onEffectTick(player);
            }
        });
    }

    private static void addRole(Role role) {
        roles.put(role.name, role);
        rolesOrdered.add(role);
        int numPlayerTasks = role.getPlayerTaskPool().size();
        int numGlobalTasks = role.getGlobalTasks().size();
        if (numPlayerTasks != PLAYER_TASKS_PER_ROLE)
            Titanomach.MOD_LOGGER.warn("[{}] has {} player tasks registered (Expected {})", role.name, numPlayerTasks, PLAYER_TASKS_PER_ROLE);
        if (numGlobalTasks != GLOBAL_TASKS_PER_ROLE)
            Titanomach.MOD_LOGGER.warn("[{}] has {} global tasks registered (Expected {})", role.name, numGlobalTasks, GLOBAL_TASKS_PER_ROLE);
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

    public static List<Role> getPreviousRoles(Role role) {
        return rolesOrdered.subList(0, rolesOrdered.indexOf(role));
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
