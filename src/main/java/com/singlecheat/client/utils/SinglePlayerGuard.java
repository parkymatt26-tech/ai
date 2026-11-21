package com.singlecheat.client.utils;

import com.singlecheat.client.CheatClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/**
 * The core safety layer: SingleCheat only works in single player.
 *
 * <p>A world counts as single player only when the client is running its own
 * integrated server. There is deliberately no address/string heuristic here -
 * a multiplayer server hosted on localhost must still be treated as
 * multiplayer, otherwise the lock would be trivially bypassable.
 */
public final class SinglePlayerGuard {
    private static final long WARNING_COOLDOWN_MS = 5000L;
    private static long lastWarningTime = 0L;

    private SinglePlayerGuard() {}

    /** @return true only for a single-player (integrated server) world. */
    public static boolean isSinglePlayer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return false;
        if (client.player == null || client.world == null) return false;

        // Vanilla check: true when an integrated server is running.
        if (client.isInSingleplayer()) return true;

        // Extra belt-and-braces check: an integrated server object only exists
        // when the client hosts the world itself.
        return client.getServer() != null;
    }

    /** Alias kept for readability at call sites. */
    public static boolean canUseCheats() {
        return isSinglePlayer();
    }

    /**
     * Kills every module and warns the player. Called from the client tick loop
     * as soon as a non-single-player connection is detected.
     */
    public static void enforceSinglePlayer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        if (CheatClient.moduleManager != null) {
            CheatClient.moduleManager.disableAll();
        }

        if (client.player == null) return;

        long now = System.currentTimeMillis();
        if (now - lastWarningTime < WARNING_COOLDOWN_MS) return;
        lastWarningTime = now;

        client.player.sendMessage(Text.of(
                "§c[SingleCheat] §fThis client is §lSINGLE-PLAYER ONLY§r§f. All modules have been disabled."), false);
        client.player.sendMessage(Text.of(
                "§c§lSingleCheat§r §fMultiplayer detected - cheats locked."), true);
    }
}
