package net.mob_armor_trims.majo24.forge.config;

import dev.isxander.yacl3.api.controller.ValueFormatter;
import net.minecraft.network.chat.Component;

public class Formatters {
    private Formatters() {}

    public static class IntegerToPercentage implements ValueFormatter<Integer> {
        @Override
        public Component format(Integer value) {
            return Component.literal(value.toString() + "%");
        }
    }

    public static class TrimSystem implements ValueFormatter<net.mob_armor_trims.majo24.config.TrimSystem> {
        @Override
        public Component format(net.mob_armor_trims.majo24.config.TrimSystem value) {
            switch (value) {
                case RANDOM_TRIMS -> {
                    return Component.literal("Random Trims");
                }
                case CUSTOM_TRIMS -> {
                    return Component.literal("Custom Trims");
                }
                default -> {
                    return Component.literal("Error: Unknown Trim System");
                }
            }
        }
    }
}
