package net.majo24.mob_armor_trims.config.configscreen;

import dev.isxander.yacl3.api.controller.ValueFormatter;
import net.majo24.mob_armor_trims.config.Config;
import net.minecraft.network.chat.Component;

public class Formatters {
    private Formatters() {}

    public static class IntegerToPercentage implements ValueFormatter<Integer> {
        @Override
        public Component format(Integer value) {
            return Component.literal(value.toString() + "%");
        }
    }

    public static class TrimSystem implements ValueFormatter<Config.TrimSystems> {
        @Override
        public Component format(Config.TrimSystems selectedSystem) {
            return switch (selectedSystem) {
                case RANDOM_TRIMS -> Component.literal("Random Trims");
                case CUSTOM_TRIM_COMBINATIONS -> Component.literal("Custom Trim Combinations");
                case NONE -> Component.literal("Disabled");
            };
        }
    }
}