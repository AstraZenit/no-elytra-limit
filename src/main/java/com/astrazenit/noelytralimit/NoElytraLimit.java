package com.astrazenit.noelytralimit;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;

public class NoElytraLimit implements ModInitializer {
    // Si más adelante usás GROWTH_RATE, reintroducila. Por ahora la quité para eliminar warnings.
    public static boolean ENABLED = true;
    public static double MAX_SPEED = 300.0;
    public static double ROCKET_MULTIPLIER = 1.5;

    private static MinecraftServer server;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            server = minecraftServer;
            applyGameRule();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(minecraftServer -> {
            server = null;
            BoostState.clearAll();
            SpeedometerManager.clear();
        });

        ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> {
            for (ServerPlayer player : minecraftServer.getPlayerList().getPlayers()) {
                if (ENABLED) {
                    BoostState.tickPlayer(player);
                }
                SpeedometerManager.tick(player); // Update the speedometer every tick
            }
        });

        // Command registration /elytralimit
        // Convertimos la lambda a expression-lambda y marcamos los parámetros no usados con prefijo _
        CommandRegistrationCallback.EVENT.register((dispatcher, _registryAccess, _environment) ->
                dispatcher.register(
                        Commands.literal("elytralimit")
                                .then(Commands.literal("on").executes(ctx -> {
                                    ENABLED = true;
                                    BoostState.clearAll();
                                    applyGameRule();
                                    ctx.getSource().sendSuccess(
                                            () -> Component.literal("NoElytraLimit: ENABLED"), true);
                                    return 1;
                                }))
                                .then(Commands.literal("off").executes(ctx -> {
                                    ENABLED = false;
                                    BoostState.clearAll();
                                    applyGameRule();
                                    ctx.getSource().sendSuccess(
                                            () -> Component.literal("NoElytraLimit: DISABLED"), true);
                                    return 1;
                                }))
                                .then(Commands.literal("speedo").executes(ctx -> {
                                    SpeedometerManager.ENABLED = !SpeedometerManager.ENABLED;
                                    ctx.getSource().sendSuccess(
                                            () -> Component.literal("Built-in Speedometer: " + (SpeedometerManager.ENABLED ? "§aON" : "§cOFF")), false);
                                    return 1;
                                }))
                                .then(Commands.literal("status").executes(ctx -> {
                                    String msg = "NoElytraLimit: " + (ENABLED ? "ON" : "OFF")
                                            + " | MAX_SPEED=" + MAX_SPEED
                                            + " | Multiplier=" + ROCKET_MULTIPLIER
                                            + " | Speedo=" + (SpeedometerManager.ENABLED ? "ON" : "OFF");
                                    ctx.getSource().sendSuccess(() -> Component.literal(msg), false);
                                    return 1;
                                }))
                                .then(Commands.literal("speed")
                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0))
                                                .executes(ctx -> {
                                                    MAX_SPEED = DoubleArgumentType.getDouble(ctx, "value");
                                                    ctx.getSource().sendSuccess(
                                                            () -> Component.literal("MAX_SPEED = " + MAX_SPEED + " bps"), true);
                                                    return 1;
                                                })))
                                .then(Commands.literal("multiplier")
                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(1.1))
                                                .executes(ctx -> {
                                                    ROCKET_MULTIPLIER = DoubleArgumentType.getDouble(ctx, "value");
                                                    ctx.getSource().sendSuccess(
                                                            () -> Component.literal("ROCKET_MULTIPLIER = " + ROCKET_MULTIPLIER + "x"), true);
                                                    return 1;
                                                })))
                )
        );
    }

    public static void applyGameRule() {
        if (server == null) return;
        boolean antiCheat = !ENABLED;
        server.getGameRules().set(GameRules.ELYTRA_MOVEMENT_CHECK, antiCheat, server);
    }
}