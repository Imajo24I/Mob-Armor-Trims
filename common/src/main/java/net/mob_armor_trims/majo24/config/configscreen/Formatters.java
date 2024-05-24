package net.mob_armor_trims.majo24.config.configscreen;

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
            return switch (value) {
                case RANDOM_TRIMS -> Component.literal("Random Trims");
                case CUSTOM_TRIMS -> Component.literal("Custom Trims");
                case NONE -> Component.literal("Disabled");
            };
        }
    }
}