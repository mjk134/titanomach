package io.github.mjk134.titanomach.server.sacrifice;

import com.google.common.base.Stopwatch;
import com.mojang.datafixers.util.Pair;
import io.github.mjk134.titanomach.server.TitanomachPlayer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.time.Duration;
import java.util.*;

import static io.github.mjk134.titanomach.Titanomach.CONFIG;

/**
 * Handles voting - is only static because it is session based
 */
public final class  VoteManager {
    /// 5 players must be online for it to go forward
    public static boolean isOngoing = false;
    public static HashMap<UUID, UUID> playerVoteMap = new HashMap<>();
    private static final Random random = new Random();
    /// Basically they need to be online for at least 5 minutes for their vote to count -> otherwise they can get excluded
    public static HashMap<UUID, Stopwatch> playerVoteStopwatches = new HashMap<>();
    /// Some players may choose to sacrifice an item to be excluded from the vote -> this can be done by gui
    public static List<UUID> sacrificedItemPlayers = new ArrayList<>();

    public static boolean meetsRequirements(PlayerManager playerManager) {
        List<ServerPlayerEntity> players = playerManager.getPlayerList();
        VoteManager.isOngoing = players.size() >= 5;
        return VoteManager.isOngoing;
    }

    /// I was gonna use this but i forgot where
    public static boolean meetsRequirements() {
        return isOngoing;
    }

    public static void putVote(ServerPlayerEntity votingPlayer, ServerPlayerEntity votedForPlayer) {
        playerVoteMap.put(votingPlayer.getUuid(), votedForPlayer.getUuid());
    }

    public static void resumeStopwatch(ServerPlayerEntity player) {
        // check if player has stopwatch
        if (playerVoteStopwatches.containsKey(player.getUuid())) {
            playerVoteStopwatches.get(player.getUuid()).start();
        } else {
            playerVoteStopwatches.put(player.getUuid(), Stopwatch.createStarted());
        }
    }

    public static boolean isPlayerExempt(UUID uuid) {
        if (sacrificedItemPlayers.contains(uuid)) {
            return true;
        } else if (playerVoteStopwatches.containsKey(uuid)) {
            Stopwatch stopwatch = playerVoteStopwatches.get(uuid);
            Duration duration = stopwatch.elapsed();
            return duration.getSeconds() < 300;
        }
        return false;
    }

    public static void stopStopwatch(ServerPlayerEntity player) {
        Stopwatch stopwatch = playerVoteStopwatches.get(player.getUuid());
        stopwatch.stop();
        // Check if it is timing correctly: MOD_LOGGER.info(Long.toString(stopwatch.elapsed().getSeconds()));
    }

    // This is ran on /stop command, so we can have custom logic before shutting down the server. ig this could be implemented using a mixin but idk
    public static UUID getSacrificalPlayerUUID() {
        HashMap<UUID, Integer> voteCount = new HashMap<>();
        for (UUID uuid : playerVoteMap.values()) {
            if (VoteManager.isPlayerExempt(uuid)) continue;
            if (!voteCount.containsKey(uuid)) {
                voteCount.put(uuid, 1);
            } else {
                int newCount = voteCount.get(uuid) + 1;
                voteCount.put(uuid, newCount);
            }
        }


        List<Pair<UUID, Integer>> highestVoteCounts = new ArrayList<>();
        for (UUID key : voteCount.keySet()) {
            int voteAmount = voteCount.get(key);
            if (highestVoteCounts.isEmpty()) {
                highestVoteCounts.add(new Pair<>(key, voteAmount));
            }
            // Perhaps a better way to do this iteration/comparison but this should work for now
            for (Pair<UUID, Integer> kv : highestVoteCounts) {
                if (voteAmount > kv.getSecond()) {
                    // clear the list and append the new pair
                    highestVoteCounts.clear();
                    highestVoteCounts.add(new Pair<>(key, voteAmount));
                } else if (voteAmount == kv.getSecond()) {
                    highestVoteCounts.add(new Pair<>(key, voteAmount));
                }
            }
        }

        if (highestVoteCounts.size() == 1) {
            // Confusing but it's just getting the first element of the array and then the first element of the pair which is the uuid
            return highestVoteCounts.getFirst().getFirst();
        }

        // Pick a random UUID from the list
        int randomIndex = random.nextInt(highestVoteCounts.size());
        return highestVoteCounts.get(randomIndex).getFirst();
    }

    public static boolean isVotePossible() {
       // Check if at least 5 people have logged on for at least 5 minutes
        int loggedOnCount = 0;
        for (Stopwatch stopwatch: playerVoteStopwatches.values()) {
            if (stopwatch.elapsed().getSeconds() > 300) loggedOnCount++;
        }
        return loggedOnCount >= 5;
    }

    public static ArrayList<UUID> getPenalizedPlayersUUID() {
        // Check if they are exempt, if they are not, and they are not in the vote map penalise them
        ArrayList<UUID> penalized = new ArrayList<>();

        for (UUID uuid : playerVoteStopwatches.keySet()){
            if (!isPlayerExempt(uuid)) {
                penalized.add(uuid);
            }
        }

        return penalized;
    }

    public static void reduceSacrificeLevels() {
        for (TitanomachPlayer p : CONFIG.getPlayers()) {
            if (p.sacrificeLevel == 0) continue;
            p.sacrificeLevel--;
        }
    }
}
