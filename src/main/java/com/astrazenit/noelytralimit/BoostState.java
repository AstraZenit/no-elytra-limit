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
        return CURRENT_MAX.getOrDefault(id, 34.0);
    }

    public static void increaseMax(UUID id, double multiplier, double hardMax) {
        double current = getCurrentMax(id);
        double next = Math.min(current * multiplier, hardMax);
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

        // Si te frenás bastante, baja la velocidad máxima actual
        if (speed < RESET_SPEED_THRESHOLD) {
            CURRENT_MAX.remove(id);
        } else if (speed < getCurrentMax(id) * 0.6) {
            // Baja de a poco si vas más lento que tu máximo actual
            double current = getCurrentMax(id);
            CURRENT_MAX.put(id, Math.max(VANILLA_ROCKET_SPEED, current * 0.85));
        }
    }
}