package net.mob_armor_trims.majo24.config.ConfigScreen;

import dev.isxander.yacl3.api.controller.ValueFormatter;
import net.minecraft.network.chat.Component;
import net.mob_armor_trims.majo24.config.Config;

public class Formatters {
    private Formatters() {}

    public static class IntegerToPercentage implements ValueFormatter<Integer> {
        @Override
        public Component format(Integer value) {
            return Component.literal(value.toString() + "%");
        }
    }

    public static class TrimSystem implements ValueFormatter<Config.TrimSystem> {
        @Override
        public Component format(Config.TrimSystem value) {
            switch (value) {
                case RANDOM_TRIMS -> {
                    return Component.literal("Random Trims");
                }
                case CUSTOM_TRIMS -> {
                    return Component.literal("Custom Trims");
                }
                case NONE -> {
                    return Component.literal("Disabled");
                }
                default -> {
                    return Component.literal("Error: Unknown Trim System");
                }
            }
        }
    }
}