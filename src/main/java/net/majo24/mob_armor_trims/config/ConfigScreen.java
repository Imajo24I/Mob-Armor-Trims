package net.majo24.mob_armor_trims.config;

import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.majo24.mob_armor_trims.MobArmorTrims;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen {
    private ConfigScreen() {}

    public static Screen getConfigScreen(Screen parent) {
           ConfigBuilder builder = ConfigBuilder.create()
        .setParentScreen(parent)
        .setTitle(Text.literal("Mob Armor Trims"));
        builder.setSavingRunnable(MobArmorTrims.configManager::saveConfig);

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startIntSlider(Text.literal("Trim Chance"), MobArmorTrims.configManager.getTrimChance(), 0, 100)
                        .setDefaultValue(ConfigManager.DEFAULT_TRIM_CHANCE)
                        .setTooltip(Text.literal("Chance of each armor piece of a mob having an armor trim"))
                        .setSaveConsumer(MobArmorTrims.configManager::setTrimChance)
                        .build());
        return builder.build();
    }
}
