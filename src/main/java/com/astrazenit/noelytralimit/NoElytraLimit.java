package com.astrazenit.noelytralimit;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;

public class NoElytraLimit implements ModInitializer {
    public static final double GROWTH_RATE = 1.04;

    // Estos valores los podés cambiar desde el menú de mods (si agregaste Cloth Config)
    public static double MAX_SPEED = 300.0;
    public static double ROCKET_MULTIPLIER = 1.5;   // ← esta es la nueva

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
                server.getGameRules().set(GameRules.ELYTRA_MOVEMENT_CHECK, false, server)
        );

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                BoostState.tickPlayer(player);
            }
        });
    }
}