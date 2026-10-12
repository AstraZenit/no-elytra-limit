package com.astrazenit.noelytralimit;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.literal("No Elytra Limit"));

            ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            general.addEntry(entryBuilder.startDoubleField(
                            Component.literal("Max Speed (0 = Infinite)"),
                            NoElytraLimit.MAX_SPEED)
                    .setDefaultValue(300.0)
                    .setMin(0.0) // Allow 0 for no limit
                    .setSaveConsumer(value -> NoElytraLimit.MAX_SPEED = value)
                    .build());

            general.addEntry(entryBuilder.startDoubleField(
                            Component.literal("Rocket Multiplier"),
                            NoElytraLimit.ROCKET_MULTIPLIER)
                    .setDefaultValue(1.5)
                    .setMin(1.1)
                    .setSaveConsumer(value -> NoElytraLimit.ROCKET_MULTIPLIER = value)
                    .build());

            return builder.build();
        };
    }
}