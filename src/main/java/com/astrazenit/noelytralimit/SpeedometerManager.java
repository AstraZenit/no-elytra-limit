package com.astrazenit.noelytralimit;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpeedometerManager {
    public static boolean ENABLED = false;
    private static final Map<UUID, Vec3> lastPositions = new HashMap<>();

    public static void tick(ServerPlayer player) {
        if (!ENABLED) {
            lastPositions.remove(player.getUUID());
            return;
        }

        if (!player.isFallFlying()) {
            lastPositions.remove(player.getUUID());
            return;
        }

        UUID id = player.getUUID();
        Vec3 currentPos = player.position();
        Vec3 lastPos = lastPositions.getOrDefault(id, currentPos);

        // Real position-based speed (the one datapacks measure)
        double realBps = currentPos.distanceTo(lastPos) * 20.0;
        // Speed based on the mod's internal velocity vector
        double velBps = player.getDeltaMovement().length() * 20.0;

        lastPositions.put(id, currentPos);

        // If the mod is off -> vanilla limit. If MAX_SPEED <= 0 -> infinite.
        String maxStr;
        if (!NoElytraLimit.ENABLED) {
            maxStr = "Vanilla";
        } else if (NoElytraLimit.MAX_SPEED <= 0.0) {
            maxStr = "Infinite";
        } else {
            maxStr = String.format("%.0f", NoElytraLimit.MAX_SPEED);
        }

        String msg = String.format("§bVel: §f%.1f bps §e| §aReal (Pos): §f%.1f bps §7(Max: %s)",
                velBps, realBps, maxStr);

        // Send the clean message to the player's Action Bar
        player.connection.send(new ClientboundSetActionBarTextPacket(Component.literal(msg)));
    }

    public static void clear() {
        lastPositions.clear();
    }
}