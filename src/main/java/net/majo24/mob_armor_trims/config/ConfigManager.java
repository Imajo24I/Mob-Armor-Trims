package net.majo24.mob_armor_trims.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import net.majo24.mob_armor_trims.MobArmorTrims;
import net.majo24.mob_armor_trims.config.annotations.Entry;
import net.majo24.mob_armor_trims.config.annotations.SubConfig;
import net.majo24.mob_armor_trims.config.entries.ConfigEntry;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;

public class ConfigManager<T> {
    private T config;
    private final T defaultConfig;

    private final Path configPath;

    public ConfigManager(Class<T> configClass, Path configPath) {
        Constructor<T> noArgsConstructor;
        try {
            noArgsConstructor = configClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new ClassFormatError("Failed to find no-args constructor for config class %s.".formatted(configClass.getName()) + "\n" + e);
        }

        this.defaultConfig = loadDefaultConfig(noArgsConstructor);
        this.config = defaultConfig;
        this.configPath = configPath;
    }

    public T getConfig() {
        return config;
    }

    public void saveConfigToFile() {
        MobArmorTrims.LOGGER.info("Saving Mob Armor Trims config to file");
        CommentedFileConfig fileConfig = fileConfigFromConfig(config, configPath);
        fileConfig.save();
    }

    public static <T> T loadDefaultConfig(Constructor<T> noArgsConstructor) {
        try {
            return noArgsConstructor.newInstance();
        } catch (Exception e) {
            throw new ClassFormatError("Failed to load default config for class %s.".formatted(noArgsConstructor.getDeclaringClass().getName()) + "\n" + e);
        }
    }

    public void loadFromFile() {
        if (Files.exists(configPath)) {
            // Get config from file
            try {
                CommentedFileConfig fileConfig = CommentedFileConfig.of(configPath.toFile());
                fileConfig.load();
                config = configFromFile(fileConfig);
            } catch (Exception e) {
                invalidConfigCrash(e, configPath);
                config = defaultConfig;
            }
        } else {
            // Create a new Config
            MobArmorTrims.LOGGER.info("Creating Mob Armor Trims config file");
            try {
                Files.createFile(configPath);
                CommentedFileConfig fileConfig = fileConfigFromConfig(defaultConfig, configPath);
                fileConfig.save();
            } catch (Exception e) {
                MobArmorTrims.LOGGER.error("Could not create Mob Armor Trims config file. Using default config.", e);
            }
            this.config = defaultConfig;
        }
    }

    public CommentedFileConfig fileConfigFromConfig(T config, Path configPath) {
        CommentedFileConfig fileConfig = CommentedFileConfig.of(new File(configPath.toString()));
        recursivelyAddToFileConfig(fileConfig, config);
        return fileConfig;
    }

    private void recursivelyAddToFileConfig(CommentedConfig fileConfig, Object config) {
        for (Field field : config.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Entry.class)) {
                ensureConfigFieldIsPublic(field);

                ConfigEntry<?> entry = (ConfigEntry<?>) getFieldValue(field, config);
                Entry entryAnnotation = field.getAnnotation(Entry.class);

                fileConfig.add(entryAnnotation.name(), entry.getValue());
                fileConfig.setComment(entryAnnotation.name(), entryAnnotation.description());
            } else if (field.isAnnotationPresent(SubConfig.class)) {
                ensureConfigFieldIsPublic(field);

                CommentedConfig subConfig = fileConfig.createSubConfig();
                SubConfig subConfigAnnotation = field.getAnnotation(SubConfig.class);

                recursivelyAddToFileConfig(subConfig, getFieldValue(field, config));
                fileConfig.add(subConfigAnnotation.name(), subConfig);
                fileConfig.setComment(subConfigAnnotation.name(), subConfigAnnotation.description());
            }
        }
    }

    public T configFromFile(CommentedFileConfig fileConfig) {
        T newConfig = this.defaultConfig;
        recursivelyAddToConfig(newConfig, fileConfig, fileConfig.getNioPath());
        return newConfig;
    }

    private <E> void recursivelyAddToConfig(Object config, Config fileConfig, Path filePath) {
        for (Field field : config.getClass().getDeclaredFields())
            if (field.isAnnotationPresent(Entry.class)) {
                ensureConfigFieldIsPublic(field);

                try {
                    ConfigEntry<E> entry = (ConfigEntry<E>) getFieldValue(field, config);
                    String entryName = field.getAnnotation(Entry.class).name();
                    E entryValue = getAndValidateConfigEntry(entryName,
                            getterForEntry(fileConfig, entry, entryName),
                            entry.getDefaultValue(),
                            filePath);
                    entry.setValue(entryValue);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (field.isAnnotationPresent(SubConfig.class)) {
                ensureConfigFieldIsPublic(field);

                recursivelyAddToConfig(getFieldValue(field, config), fileConfig.get(field.getAnnotation(SubConfig.class).name()), filePath);
            }
    }

    private <E> Supplier<E> getterForEntry(Config config, ConfigEntry<E> entry, String entryName) {
        if (entry.getDefaultValue() instanceof Enum<?>) {
            return () -> (E) config.getEnum(entryName, ((Enum) entry.getDefaultValue()).getDeclaringClass());
        }
        return () -> config.get(entryName);
    }
     private static <E> E getAndValidateConfigEntry(String configName, Supplier<E> supplier, E defaultValue, Path configPath) {
        try {
            return Objects.requireNonNull(supplier.get());
        } catch (Exception e) {
            MobArmorTrims.LOGGER.error("Failed to load config option \"{}\" from Mob Armor Trims config file. Using the default value \"{}\" for this session. Please ensure the entry and the config file are valid. You can reset the config file by deleting the file. It is located under \"{}\".", configName, defaultValue, configPath, e);
            return defaultValue;
        }
    }

    private Object getFieldValue(Field field, Object instance) {
        try {
            return field.get(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void ensureConfigFieldIsPublic(Field field) {
        if (!Modifier.isPublic(field.getModifiers())) {
            throw new IllegalStateException("Config field " + field.getName() + " located in " + config.getClass().getName() + " is not public.");
        }
    }

    private static void invalidConfigCrash(Exception e, Path configPath) {
        Minecraft.getInstance().delayCrash(new CrashReport("Failed to load Mob Armor Trims config from file.", new IllegalStateException("Failed to load Mob Armor Trims config from file. Please make sure your config file is valid. You can reset it by deleting the file. It is located under " + configPath + ".\n" + e.getMessage())));
    }
}
