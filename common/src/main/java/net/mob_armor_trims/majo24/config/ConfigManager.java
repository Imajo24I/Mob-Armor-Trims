package net.mob_armor_trims.majo24.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.mob_armor_trims.majo24.MobArmorTrims;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ConfigManager {
    public static final Config.TrimSystem DEFAULT_ENABLED_SYSTEM = Config.TrimSystem.RANDOM_TRIMS;
    public static final int DEFAULT_TRIM_CHANCE = 50;
    public static final int DEFAULT_SIMILAR_TRIM_CHANCE = 75;
    public static final int DEFAULT_NO_TRIMS_CHANCE = 25;

    public static final List<Config.CustomTrim> DEFAULT_CUSTOM_TRIMS_LIST = new ArrayList<>();
    public static final boolean DEFAULT_APPLY_TO_ENTIRE_ARMOR = true;

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
            CommentedFileConfig fileConfig = CommentedFileConfig.of(configPath.toFile());
            fileConfig.load();
            return configFromFileConfig(fileConfig);
        } else {
            // Create a new Config
            Config newConfig = getDefaultConfig();
            try {
                MobArmorTrims.LOGGER.info("Creating Mob Armor Trims config file");
                Files.createFile(configPath);
                CommentedFileConfig fileConfig = fileConfigFromConfig(newConfig, configPath);
                fileConfig.save();
            } catch (IOException e) {
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

        customTrims.add("apply_to_entire_armor", config.getApplyToEntireArmor());
        customTrims.setComment("apply_to_entire_armor", "Should the custom armor trim be applied to the entire armor.\nIf false, a new custom trim will be chosen for each armor piece");

        customTrims.add("custom_trims_list", Config.CustomTrim.toStringList(config.getCustomTrimsList()));
        customTrims.setComment("custom_trims_list", """
            The list of custom trims.
           
            To create a new custom trim, add a new list with two String fields inside the brackets. For example: [["", ""]]
            Make sure to have it separated with a comma from other custom trims.
           
            In the left string field, enter a valid trim material.
            As an example: "quartz".
           
            In the right string field, enter a valid trim pattern.
            As an example: "silence"
            
            To not have to specify the whole trim pattern, you can leave out the "_armor_trim_smithing_template" part of the pattern, as it is the same for every pattern.""");

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

        fileConfig.add("custom_trims", customTrims);
        fileConfig.setComment("custom_trims", "Settings for the Custom Trims system.\nThese settings will only make a difference, if the CUSTOM_TRIMS system is enabled");

        fileConfig.add("stacked_trims", stackedTrims);
        fileConfig.setComment("stacked_trims", "Settings for the Stacked Armor Trims Mod Compatibility.\nThese settings will only make a difference, if the STACKED_TRIMS system is enabled and the stacked armor trims mod is used");
        return fileConfig;
    }

    private static Config configFromFileConfig(CommentedFileConfig fileConfig) {
        try {
            com.electronwill.nightconfig.core.Config generalCategory = fileConfig.get("general");
            com.electronwill.nightconfig.core.Config randomTrimsCategory = fileConfig.get("random_trims");
            com.electronwill.nightconfig.core.Config customTrimsCategory = fileConfig.get("custom_trims");
            com.electronwill.nightconfig.core.Config stackedTrimsCategory = fileConfig.get("stacked_trims");

            return new Config(
                    Config.TrimSystem.valueOf(generalCategory.get("enabled_system")),
                    randomTrimsCategory.get("trim_chance"),
                    randomTrimsCategory.get("similar_trim_chance"), generalCategory.get("no_trims_chance"),
                    Config.CustomTrim.fromList(customTrimsCategory.get("custom_trims_list")), customTrimsCategory.get("apply_to_entire_armor"),
                    stackedTrimsCategory.get("stacked_trim_chance"), stackedTrimsCategory.get("max_stacked_trims")
            );
        } catch (Exception e) {
            MobArmorTrims.LOGGER.error("Failed to load Mob Armor Trims config from file. Please make sure your config file is valid. You can reset it by deleting the file. It is located under .minecraft/config/mob_armor_trims.toml");
            throw e;
        }
    }

    public static Config getDefaultConfig() {
        return new Config(DEFAULT_ENABLED_SYSTEM, DEFAULT_TRIM_CHANCE, DEFAULT_SIMILAR_TRIM_CHANCE, DEFAULT_NO_TRIMS_CHANCE,
                DEFAULT_CUSTOM_TRIMS_LIST, DEFAULT_APPLY_TO_ENTIRE_ARMOR,
                DEFAULT_STACKED_TRIM_CHANCE, DEFAULT_MAX_STACKED_TRIMS);
    }

    public void saveConfig() {
        MobArmorTrims.LOGGER.info("Saving Mob Armor Trims config to file");
        CommentedFileConfig fileConfig = fileConfigFromConfig(this.config, configPath);
        fileConfig.save();
    }

    public Config.TrimSystem getEnabledSystem() {
        return this.config.getEnabledSystem();
    }

    public void setEnabledSystem(Config.TrimSystem enabledSystem) {
        this.config.setEnabledSystem(enabledSystem);
    }

    public int getTrimChance() {
        return this.config.getTrimChance();
    }

    public void setTrimChance(int trimChance) {
        this.config.setTrimChance(trimChance);
    }

    public int getSimilarTrimChance() { return this.config.getSimilarTrimChance(); }

    public void setSimilarTrimChance(int sameTrimChance) { this.config.setSimilarTrimChance(sameTrimChance); }

    public int getNoTrimsChance() { return this.config.getNoTrimsChance(); }

    public void setNoTrimsChance(int noTrimsChance) { this.config.setNoTrimsChance(noTrimsChance); }

    /**
     * @return Returns random Custom Trim out of the Custom Trims List
    */
    @Nullable
    public Config.CustomTrim getCustomTrim(RandomSource random) {
        List<Config.CustomTrim> customTrimsList = this.config.getCustomTrimsList();
        if (customTrimsList.isEmpty()) {
            return null;
        } else {
            return customTrimsList.get(random.nextInt(customTrimsList.size()));
        }
    }

    public List<Config.CustomTrim> getCustomTrimsList() {
        return config.getCustomTrimsList();
    }

    public void setCustomTrimsList(List<Config.CustomTrim> customTrimsList) {
        this.config.setCustomTrimsList(customTrimsList);
    }

    public void addCustomTrimToCache(String material, String pattern, ArmorTrim trim) {
        this.cachedCustomTrims.put(Arrays.asList(material, pattern), trim);
    }

    @Nullable
    public ArmorTrim getOrCreateCachedCustomTrim(String material, String pattern, RegistryAccess registryAccess) {
        ArmorTrim cachedTrim = this.cachedCustomTrims.get(Arrays.asList(material, pattern));
        if (cachedTrim == null) {
            ArmorTrim newTrim = new Config.CustomTrim(material, pattern).getTrim(registryAccess);
            if (newTrim == null) {
                return null;
            }
            this.addCustomTrimToCache(material, pattern, newTrim);
            return newTrim;
        }
        return cachedTrim;
    }

    public boolean getApplyToEntireArmor() {
        return this.config.getApplyToEntireArmor();
    }

    public void setApplyToEntireArmor(boolean applyToEntireArmor) {
        this.config.setApplyToEntireArmor(applyToEntireArmor);
    }

    public int getStackedTrimChance() {
        return this.config.getStackedTrimChance();
    }

    public void setStackedTrimChance(int stackedTrimChance) { this.config.setStackedTrimChance(stackedTrimChance); }

    public int getMaxStackedTrims() {return this.config.getMaxStackedTrims();}

    public void setMaxStackedTrims(int maxStackedTrims) { this.config.setMaxStackedTrims(maxStackedTrims); }
}