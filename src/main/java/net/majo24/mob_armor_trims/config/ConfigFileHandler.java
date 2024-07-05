package net.majo24.mob_armor_trims.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.majo24.mob_armor_trims.MobArmorTrims;
import net.majo24.mob_armor_trims.config.custom_trim_combinations.TrimCombination;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public record ConfigFileHandler(Path configPath) {

    public static Config getConfigFromFile(Path configPath) {
        if (Files.exists(configPath)) {
            // Get config from file
            MobArmorTrims.LOGGER.info("Reading Mob Armor Trims config file");
            try {
                CommentedFileConfig fileConfig = CommentedFileConfig.of(configPath.toFile());
                fileConfig.load();
                return configFromFileConfig(fileConfig);
            } catch (Exception e) {
                invalidConfigCrash(e, configPath);
                return ConfigManager.getDefaultConfig();
            }
        } else {
            // Create a new Config
            Config config = ConfigManager.getDefaultConfig();
            try {
                MobArmorTrims.LOGGER.info("Creating Mob Armor Trims config file");
                Files.createFile(configPath);
                CommentedFileConfig fileConfig = fileConfigFromConfig(config, configPath);
                fileConfig.save();
            } catch (Exception e) {
                MobArmorTrims.LOGGER.error("Could not create Mob Armor Trims config file. Using default config.", e);
            }
            return config;
        }
    }

    private static Config configFromFileConfig(CommentedFileConfig fileConfig) {
        try {
            com.electronwill.nightconfig.core.Config generalCategory = fileConfig.get("general");
            com.electronwill.nightconfig.core.Config randomTrimsCategory = fileConfig.get("random_trims");
            com.electronwill.nightconfig.core.Config customTrimCombinationsCategory = fileConfig.get("trim_combinations");
            com.electronwill.nightconfig.core.Config stackedTrimsCategory = fileConfig.get("stacked_trims");

            Config.TrimSystems enabledSystem = getAndValidateConfigEntry("enabled_system", () -> generalCategory.getEnum("enabled_system", Config.TrimSystems.class), ConfigManager.DEFAULT_ENABLED_SYSTEM, fileConfig.getNioPath());
            int trimChance = getAndValidateConfigEntry("trim_chance", () -> randomTrimsCategory.get("trim_chance"), ConfigManager.DEFAULT_TRIM_CHANCE, fileConfig.getNioPath());
            int similarTrimChance = getAndValidateConfigEntry("similar_trim_chance", () -> randomTrimsCategory.get("similar_trim_chance"), ConfigManager.DEFAULT_SIMILAR_TRIM_CHANCE, fileConfig.getNioPath());
            int noTrimsChance = getAndValidateConfigEntry("no_trims_chance", () -> generalCategory.get("no_trims_chance"), ConfigManager.DEFAULT_NO_TRIMS_CHANCE, fileConfig.getNioPath());
            List<TrimCombination> trimCombinations = getAndValidateConfigEntry("custom_trim_combinations", () -> TrimCombination.trimCombinationsFromStringList(customTrimCombinationsCategory.get("custom_trim_combinations")), ConfigManager.DEFAULT_TRIM_COMBINATIONS, fileConfig.getNioPath());
            int stackedTrimChance = getAndValidateConfigEntry("stacked_trim_chance", () -> stackedTrimsCategory.get("stacked_trim_chance"), ConfigManager.DEFAULT_STACKED_TRIM_CHANCE, fileConfig.getNioPath());
            int maxStackedTrims = getAndValidateConfigEntry("max_stacked_trims", () -> stackedTrimsCategory.get("max_stacked_trims"), ConfigManager.DEFAULT_MAX_STACKED_TRIMS, fileConfig.getNioPath());

            return new Config(
                    enabledSystem,
                    trimChance,
                    similarTrimChance, noTrimsChance,
                    trimCombinations,
                    stackedTrimChance, maxStackedTrims
            );
        } catch (Exception e) {
            invalidConfigCrash(e, fileConfig.getNioPath());
            return ConfigManager.getDefaultConfig();
        }
    }

    private static <T> T getAndValidateConfigEntry(String configName, Supplier<T> supplier, T defaultValue, Path configPath) {
        try {
            return Objects.requireNonNull(supplier.get());
        } catch (Exception e) {
            MobArmorTrims.LOGGER.error("Failed to load Mob Armor Trims config entry {} from file. Using default value {} for this session. Please ensure the entry and the config file is valid. You can reset the config file by deleting the file. It is located under {}.\n", configName, defaultValue, configPath, e);
            return defaultValue;
        }
    }

    public void saveConfig(Config config) {
        MobArmorTrims.LOGGER.info("Saving Mob Armor Trims config to file");
        CommentedFileConfig fileConfig = fileConfigFromConfig(config, configPath);
        fileConfig.save();
    }

    private static CommentedFileConfig fileConfigFromConfig(Config config, Path configPath) {
        CommentedFileConfig fileConfig = CommentedFileConfig.of(new File(configPath.toString()));

        // General Subcategory
        CommentedConfig general = fileConfig.createSubConfig();

        general.add("enabled_system", config.getEnabledSystem());
        general.setComment("enabled_system", """
                Select the System of how to select, what trims to give mobs.
                - RANDOM_TRIMS: Randomly choose the trim, but also take the previous trim highly into account.
                - CUSTOM_TRIMS: Choose the trim from a list of custom trims. You can manage the trims yourself""");

        general.add("no_trims_chance", config.getNoTrimsChance());
        general.setComment("no_trims_chance", "Chance of the mob having no trims at all");

        // Random Trims system Subcategory
        CommentedConfig randomTrims = fileConfig.createSubConfig();

        randomTrims.add("trim_chance", config.getTrimChance());
        randomTrims.setComment("trim_chance", "Chance of each armor piece from a mob having an armor trim.");

        randomTrims.add("similar_trim_chance", config.getSimilarTrimChance());
        randomTrims.setComment("similar_trim_chance", "Chance of each armor piece having a similar armor trim as the previous armor piece.");

        // Custom Trim Combinations system Subcategory
        CommentedConfig customTrims = fileConfig.createSubConfig();

        customTrims.add("custom_trim_combinations", config.getTrimCombinations().stream().map(TrimCombination::toStringList).toList());
        customTrims.setComment("custom_trim_combinations", """
                The list of custom trim combinations.
                           
                To create a new trim combination, add a new list with with 5 lists inside.
                Then for the first inner list, add a String. In the rest of the inner lists, add 2 Strings.
                It should look somewhat like this: [[""], ["", ""], ["", ""], ["", ""], ["", ""]]
                Make sure to have the outer list separated with a comma from other trim combinations.
                           
                In the first lists String, enter the Armor Material, the trim combination should be applied on.
                For example: ["gold"]
                           
                For the rest of the lists, in the first String, enter a valid Trim Material.
                In the second String, enter a valid Trim Pattern
                To not have to specify the whole trim pattern, you can leave out the "_armor_trim_smithing_template" part of the pattern, as it is the same for every pattern.
                For example: ["amethyst_shard", "silence"]
                """);


        // Stacked Trims system Subcategory
        CommentedConfig stackedTrims = fileConfig.createSubConfig();

        stackedTrims.add("stacked_trim_chance", config.getStackedTrimChance());
        stackedTrims.setComment("stacked_trim_chance", "Chance of each armor piece having an additional armor trim on it.");

        stackedTrims.add("max_stacked_trims", config.getMaxStackedTrims());
        stackedTrims.setComment("max_stacked_trims", "The maximum amount of armor trims that can be stacked on each other.");

        // Add all subcategories to the main config
        fileConfig.add("general", general);
        fileConfig.setComment("general", "General Settings for the mod.");

        fileConfig.add("random_trims", randomTrims);
        fileConfig.setComment("random_trims", "Settings for the Random Trims system.\nThese settings will only make a difference, if the RANDOM_TRIMS system is enabled");

        fileConfig.add("trim_combinations", customTrims);
        fileConfig.setComment("trim_combinations", "Settings for the Custom Trim Combinations system.\nThese settings will only make a difference, if the CUSTOM_TRIM_COMBINATIONS system is enabled");

        fileConfig.add("stacked_trims", stackedTrims);
        fileConfig.setComment("stacked_trims", "Settings for the Stacked Armor Trims Mod Compatibility.\nThese settings will only make a difference, if the STACKED_TRIMS system is enabled and the stacked armor trims mod is used");
        return fileConfig;
    }

    private static void invalidConfigCrash(Exception e, Path configPath) {
        Minecraft.getInstance().delayCrash(new CrashReport("Failed to load Mob Armor Trims config from file.", new IllegalStateException("Failed to load Mob Armor Trims config from file. Please make sure your config file is valid. You can reset it by deleting the file. It is located under " + configPath + ".\n" + e.getMessage())));
    }
}
