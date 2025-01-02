package net.patrick.timer.command.timerCommand;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class PlayerTimerData {
    private static final Map<String, Integer> playerTimers = new HashMap<>(); // Store timers keyed by player UUID

    public static void save(ServerPlayerEntity player, int timerValue) {
        String playerUUID = player.getUuidAsString(); // Use UUID for unique identification
        playerTimers.put(playerUUID, timerValue); // Save the timer in memory
    }

    public static int load(ServerPlayerEntity player) {
        String playerUUID = player.getUuidAsString(); // Use UUID for unique identification
        return playerTimers.getOrDefault(playerUUID, 0); // Load the timer or default to 0
    }
}

