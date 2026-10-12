package com.astrazenit.noelytralimit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class BoostState {
    private static final Map<UUID, Double> CURRENT_MAX = new HashMap<>();
    private static final double VANILLA_ROCKET_SPEED = 34.0;
    private static final double RESET_SPEED_THRESHOLD = 20.0;

    public static double getCurrentMax(UUID id) {
        return CURRENT_MAX.getOrDefault(id, VANILLA_ROCKET_SPEED);
    }

    public static void increaseMax(UUID id, double multiplier, double hardMax) {
        double current = getCurrentMax(id);
        double next = current * multiplier;

        // If hardMax is greater than 0, apply the cap. If it's 0, it's infinite.
        if (hardMax > 0.0) {
            next = Math.min(next, hardMax);
        }

        CURRENT_MAX.put(id, next);
    }

    public static void tickPlayer(Player player) {
        UUID id = player.getUUID();

        if (!player.isFallFlying()) {
            CURRENT_MAX.remove(id);
            return;
        }

        Vec3 vel = player.getDeltaMovement();
        double speed = Math.sqrt(vel.x * vel.x + vel.y * vel.y + vel.z * vel.z) * 20.0;

        // If you slow down a lot, lower the current max speed
        if (speed < RESET_SPEED_THRESHOLD) {
            CURRENT_MAX.remove(id);
        } else if (speed < getCurrentMax(id) * 0.6) {
            // Slowly lower if you're going slower than your current max
            double current = getCurrentMax(id);
            CURRENT_MAX.put(id, Math.max(VANILLA_ROCKET_SPEED, current * 0.85));
        }
    }

    /**
     * Clears all accumulated max speeds. Called when toggling the mod on/off
     * so no leftover values remain that the rocket mixin might still read.
     */
    public static void clearAll() {
        CURRENT_MAX.clear();
    }
}