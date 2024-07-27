package net.majo24.mob_armor_trims.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
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
    private final T config;
    private final Path configPath;
    private final Constructor<T> noArgsConstructor;

    public ConfigManager(Class<T> configClass, Path configPath) {
        this.noArgsConstructor = getNoArgsConstructor(configClass);
        this.configPath = configPath;
        this.config = loadConfigFromFile();
    }

    public T getConfig() {
        return config;
    }

    public T getDefaultConfig() {
        try {
            return this.noArgsConstructor.newInstance();
        } catch (Exception e) {
            throw new ClassFormatError("Failed to load default config for class " + this.noArgsConstructor.getDeclaringClass().getName() + ".\n" + e);
        }
    }

    private Constructor<T> getNoArgsConstructor(Class<T> configClass) {
        try {
            return configClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new ClassFormatError("Failed to find no-args constructor for config class %s.".formatted(configClass.getName()) + "\n" + e);
        }
    }

    public void saveConfigToFile() {
        MobArmorTrims.LOGGER.info("Saving Mob Armor Trims config to file");
        CommentedFileConfig fileConfig = fileConfigFromConfig(config, configPath);
        fileConfig.save();
        fileConfig.close();
    }

    public T loadConfigFromFile() {
        if (Files.exists(configPath)) {
            // Get config from file
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
            MobArmorTrims.LOGGER.info("Creating Mob Armor Trims config file");
            try {
                Files.createFile(configPath);
                CommentedFileConfig fileConfig = fileConfigFromConfig(getDefaultConfig(), configPath);
                fileConfig.save();
                fileConfig.close();
            } catch (Exception e) {
                MobArmorTrims.LOGGER.error("Could not create Mob Armor Trims config file. Using default config.", e);
            }
            return getDefaultConfig();
        }
    }

    public CommentedFileConfig fileConfigFromConfig(T config, Path configPath) {
        CommentedFileConfig fileConfig = CommentedFileConfig.of(new File(configPath.toString()));
        recursiveAddToFileConfig(fileConfig, config);
        return fileConfig;
    }

    private void recursiveAddToFileConfig(CommentedConfig fileConfig, Object config) {
        for (Field field : config.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Entry.class)) {
                ensureConfigFieldIsPublic(field);

                ConfigEntry<?> entry = (ConfigEntry<?>) getValueFromField(field, config);
                Entry entryAnnotation = field.getAnnotation(Entry.class);

                fileConfig.add(entryAnnotation.name(), entry.getValue());
                fileConfig.setComment(entryAnnotation.name(), entryAnnotation.description());
            } else if (field.isAnnotationPresent(SubConfig.class)) {
                ensureConfigFieldIsPublic(field);

                CommentedConfig subConfig = fileConfig.createSubConfig();
                SubConfig subConfigAnnotation = field.getAnnotation(SubConfig.class);

                recursiveAddToFileConfig(subConfig, getValueFromField(field, config));
                fileConfig.add(subConfigAnnotation.name(), subConfig);
                fileConfig.setComment(subConfigAnnotation.name(), subConfigAnnotation.description());
            }
        }
    }

    public T configFromFileConfig(CommentedFileConfig fileConfig) {
        T newConfig = getDefaultConfig();
        recursivelyAddToConfig(newConfig, fileConfig, fileConfig.getNioPath());
        return newConfig;
    }

    private <E> void recursivelyAddToConfig(Object config, Config fileConfig, Path filePath) {
        for (Field field : config.getClass().getDeclaredFields())
            if (field.isAnnotationPresent(Entry.class)) {
                ensureConfigFieldIsPublic(field);

                try {
                    ConfigEntry<E> entry = (ConfigEntry<E>) getValueFromField(field, config);
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

                recursivelyAddToConfig(getValueFromField(field, config), fileConfig.get(field.getAnnotation(SubConfig.class).name()), filePath);
            }
    }

    private <E> Supplier<E> getterForEntry(Config config, ConfigEntry<E> entry, String entryName) {
        if (entry.getDefaultValue() instanceof Enum<?> enumEntry) {
            return () -> (E) config.getEnum(entryName, enumEntry.getDeclaringClass());
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

    private Object getValueFromField(Field field, Object instance) {
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
