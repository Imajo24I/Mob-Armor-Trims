package net.majo24.mob_armor_trims.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.majo24.mob_armor_trims.MobArmorTrims;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ConfigManager {
    public static final Config.TrimSystems DEFAULT_ENABLED_SYSTEM = Config.TrimSystems.RANDOM_TRIMS;
    public static final int DEFAULT_TRIM_CHANCE = 50;
    public static final int DEFAULT_SIMILAR_TRIM_CHANCE = 75;
    public static final int DEFAULT_NO_TRIMS_CHANCE = 25;

    public static final List<TrimCombination> DEFAULT_TRIM_COMBINATIONS = new ArrayList<>();

    public static final int DEFAULT_STACKED_TRIM_CHANCE = 10;
    public static final int DEFAULT_MAX_STACKED_TRIMS = 3;

    private final Config config;
    private final Map<List<String>, ArmorTrim> cachedCustomTrims;

    public final Path configPath;

    public ConfigManager(Config config, Path configPath) {
        this.config = config;
        this.configPath = configPath;
        this.cachedCustomTrims = new HashMap<>();
    }

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
                return getDefaultConfig();
            }
        } else {
            // Create a new Config
            Config newConfig = getDefaultConfig();
            try {
                MobArmorTrims.LOGGER.info("Creating Mob Armor Trims config file");
                Files.createFile(configPath);
                CommentedFileConfig fileConfig = fileConfigFromConfig(newConfig, configPath);
                fileConfig.save();
            } catch (Exception e) {
                MobArmorTrims.LOGGER.error("Could not create Mob Armor Trims config file", e);
            }
            return newConfig;
        }
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

        // Custom Trims system Subcategory
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
                           
                In the rest of the lists, in the first String, enter a valid Trim Material.
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

    private static Config configFromFileConfig(CommentedFileConfig fileConfig) {
        try {
            com.electronwill.nightconfig.core.Config generalCategory = fileConfig.get("general");
            com.electronwill.nightconfig.core.Config randomTrimsCategory = fileConfig.get("random_trims");
            com.electronwill.nightconfig.core.Config customTrimCombinationsCategory = fileConfig.get("trim_combinations");
            com.electronwill.nightconfig.core.Config stackedTrimsCategory = fileConfig.get("stacked_trims");

            // For migration from old config files.
            // TODO: Handle this better
            List<TrimCombination> trimCombinations = getTrimCombinations(customTrimCombinationsCategory, fileConfig.getNioPath());

            return new Config(
                    generalCategory.getEnum("enabled_system", Config.TrimSystems.class),
                    randomTrimsCategory.get("trim_chance"),
                    randomTrimsCategory.get("similar_trim_chance"), generalCategory.get("no_trims_chance"),
                    trimCombinations,
                    stackedTrimsCategory.get("stacked_trim_chance"), stackedTrimsCategory.get("max_stacked_trims")
            );
        } catch (Exception e) {
            invalidConfigCrash(e, fileConfig.getNioPath());
            return getDefaultConfig();
        }
    }

    private static void invalidConfigCrash(Exception e, Path configPath) {
        Minecraft.getInstance().delayCrash(new CrashReport("Failed to load Mob Armor Trims config from file.", new IllegalStateException("Failed to load Mob Armor Trims config from file. Please make sure your config file is valid. You can reset it by deleting the file. It is located under " + configPath + ".\n" + e.getMessage())));
    }

    private static List<TrimCombination> getTrimCombinations(com.electronwill.nightconfig.core.Config config, Path configPath) {
        try {
            return TrimCombination.trimCombinationsFromStringList(config.get("custom_trim_combinations"));
        } catch (Exception e) {
            MobArmorTrims.LOGGER.warn("Failed to load custom trim combinations from Mob Armor Trims Config. Using default value. Please make sure your config file is valid. You can reset it by deleting the file. It is located under " + configPath, e);
            return DEFAULT_TRIM_COMBINATIONS;
        }
    }

    public static Config getDefaultConfig() {
        return new Config(DEFAULT_ENABLED_SYSTEM, DEFAULT_TRIM_CHANCE, DEFAULT_SIMILAR_TRIM_CHANCE, DEFAULT_NO_TRIMS_CHANCE,
                DEFAULT_TRIM_COMBINATIONS,
                DEFAULT_STACKED_TRIM_CHANCE, DEFAULT_MAX_STACKED_TRIMS);
    }

    public void saveConfig() {
        MobArmorTrims.LOGGER.info("Saving Mob Armor Trims config to file");
        CommentedFileConfig fileConfig = fileConfigFromConfig(this.config, configPath);
        fileConfig.save();
    }

    public Config.TrimSystems getEnabledSystem() {
        return this.config.getEnabledSystem();
    }

    public void setEnabledSystem(Config.TrimSystems enabledSystem) {
        this.config.setEnabledSystem(enabledSystem);
    }

    public int getTrimChance() {
        return this.config.getTrimChance();
    }

    public void setTrimChance(int trimChance) {
        this.config.setTrimChance(trimChance);
    }

    public int getSimilarTrimChance() {
        return this.config.getSimilarTrimChance();
    }

    public void setSimilarTrimChance(int sameTrimChance) {
        this.config.setSimilarTrimChance(sameTrimChance);
    }

    public int getNoTrimsChance() {
        return this.config.getNoTrimsChance();
    }

    public void setNoTrimsChance(int noTrimsChance) {
        this.config.setNoTrimsChance(noTrimsChance);
    }

    @Nullable
    public TrimCombination getTrimCombination(String requiredMaterial) {
        List<TrimCombination> trimCombinations = this.config.getTrimCombinations();
        if (!trimCombinations.isEmpty()) {
            Collections.shuffle(trimCombinations);
            for (TrimCombination trimCombination : trimCombinations) {
                if (trimCombination.materialToApplyTo().equals(requiredMaterial)) {
                    return trimCombination;
                }
            }
        }
        return null;
    }

    public List<TrimCombination> getTrimCombinations() {
        return config.getTrimCombinations();
    }

    public void setTrimCombinations(List<TrimCombination> trimCombinations) {
        this.config.setTrimCombinations(trimCombinations);
    }

    public void addCustomTrimToCache(String material, String pattern, ArmorTrim trim) {
        this.cachedCustomTrims.put(Arrays.asList(material, pattern), trim);
    }

    @Nullable
    public ArmorTrim getOrCreateCachedTrim(String material, String pattern, RegistryAccess registryAccess) throws IllegalStateException {
        ArmorTrim cachedTrim = this.cachedCustomTrims.get(Arrays.asList(material, pattern));
        if (cachedTrim == null) {
            ArmorTrim newTrim = new CustomTrim(material, pattern).getTrim(registryAccess);
            this.addCustomTrimToCache(material, pattern, newTrim);
            return newTrim;
        }
        return cachedTrim;
    }

    public int getStackedTrimChance() {
        return this.config.getStackedTrimChance();
    }

    public void setStackedTrimChance(int stackedTrimChance) {
        this.config.setStackedTrimChance(stackedTrimChance);
    }

    public int getMaxStackedTrims() {
        return this.config.getMaxStackedTrims();
    }

    public void setMaxStackedTrims(int maxStackedTrims) {
        this.config.setMaxStackedTrims(maxStackedTrims);
    }
}