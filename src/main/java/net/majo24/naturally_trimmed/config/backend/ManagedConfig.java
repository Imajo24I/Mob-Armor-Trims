package net.majo24.naturally_trimmed.config.backend;

import com.google.common.base.CaseFormat;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.majo24.naturally_trimmed.NaturallyTrimmed;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.JsonWriter;
import org.quiltmc.parsers.json.gson.GsonReader;
import org.quiltmc.parsers.json.gson.GsonWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class ManagedConfig<T> {
    private final Path configPath;
    private Supplier<T> defaultsGetter;

    private final Gson gson;

    public ManagedConfig(Path configPath, Supplier<T> defaultsGetter, Map<Type, Object> typeAdapters) {
        this.configPath = configPath;
        this.defaultsGetter = defaultsGetter;

        GsonBuilder builder = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .serializeNulls()
                .setPrettyPrinting();
        for (Map.Entry<Type, Object> typeAdapter : typeAdapters.entrySet()) {
            builder.registerTypeAdapter(typeAdapter.getKey(), typeAdapter.getValue());
        }
        this.gson = builder.create();
    }

    public void setDefaultGetter(Supplier<T> defaultsGetter) {
        this.defaultsGetter = defaultsGetter;
    }

    public T defaults() {
        return this.defaultsGetter.get();
    }

    private <C> void recursivelyResetToDefaults(C instance, C defaults) throws IllegalAccessException {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Entry.class)) {
                assertPublicField(field);
                field.set(instance, field.get(defaults));
            } else if (field.isAnnotationPresent(SubConfig.class)) {
                assertPublicField(field);
                recursivelyResetToDefaults(field.get(instance), field.get(defaults));
            }
        }
    }

    /**
     * Deserializes the config file and loads the instance
     */
    public void loadFromFile() {
        NaturallyTrimmed.LOGGER.info("Loading Naturally Trimmed config from config file");
        if (!Files.exists(configPath)) {
            NaturallyTrimmed.LOGGER.info("Creating new Naturally Trimmed config file with default values.");
            saveToFile();
            return;
        }

        try (JsonReader jsonReader = JsonReader.json5(configPath)) {
            GsonReader gsonReader = new GsonReader(jsonReader);
            jsonReader.beginObject();
            recursivelyDeserialize(jsonReader, gsonReader, this);
            jsonReader.endObject();

        } catch (Exception e) {
            NaturallyTrimmed.LOGGER.error("Failed to load the Naturally Trimmed config file. Using the default config for this session. To reset to the default config file, delete or rename the current one and restart the game.", e);
            try {
                this.recursivelyResetToDefaults(this, this.defaults());
            } catch (IllegalAccessException err) {
                throw new RuntimeException(err);
            }
        }
    }

    private void recursivelyDeserialize(JsonReader jsonReader, GsonReader gsonReader, Object config) throws Exception {
        Map<String, Field> fieldMap = new HashMap<>();
        Arrays.stream(config.getClass().getDeclaredFields()).forEach(field -> {
            if (field.isAnnotationPresent(Entry.class)) {
                fieldMap.put(annotationOrField(field.getAnnotation(Entry.class), field), field);
            } else if (field.isAnnotationPresent(SubConfig.class)) {
                fieldMap.put(annotationOrField(field.getAnnotation(SubConfig.class), field), field);
            }
        });

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            Field field = fieldMap.get(name);

            if (field == null) {
                // Also check for an entry with snake_case
                field = fieldMap.get(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name));
                if (field == null) {
                    jsonReader.skipValue();
                    continue;
                }
            }
            fieldMap.remove(name);
            assertPublicField(field);

            if (field.isAnnotationPresent(Entry.class)) {
                JsonElement element = this.gson.fromJson(gsonReader, JsonElement.class);

                if (element.isJsonNull()) {
                    NaturallyTrimmed.LOGGER.warn("Found null value for config field {} while deserializing config file. Using default value instead for this session", name);
                } else {
                    field.set(config, this.gson.fromJson(element, field.getGenericType()));
                }
            } else {
                jsonReader.beginObject();
                recursivelyDeserialize(jsonReader, gsonReader, field.get(config));
                jsonReader.endObject();
            }
        }
    }

    /**
     * Serializes the instance and saves it to the config file
     */
    public void saveToFile() {
        NaturallyTrimmed.LOGGER.info("Saving Naturally Trimmed config to file");

        try (StringWriter stringWriter = new StringWriter()) {
            JsonWriter jsonWriter = JsonWriter.json5(stringWriter);
            GsonWriter gsonWriter = new GsonWriter(jsonWriter);
            jsonWriter.beginObject();

            recursivelySerialize(jsonWriter, gsonWriter, this);

            jsonWriter.endObject();
            jsonWriter.flush();

            Files.writeString(this.configPath, stringWriter.toString(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (Exception e) {
            NaturallyTrimmed.LOGGER.error("Failed to save Naturally Trimmed config to file", e);
        }
    }

    private void recursivelySerialize(JsonWriter jsonWriter, GsonWriter gsonWriter, Object config) throws IOException, IllegalStateException, IllegalAccessException {
        for (Field field : config.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Entry.class)) {
                assertPublicField(field);

                Entry entry = field.getAnnotation(Entry.class);
                jsonWriter.name(annotationOrField(entry, field));
                jsonWriter.comment(Objects.requireNonNull(entry).comment());

                JsonElement element;
                try {
                    element = this.gson.toJsonTree(field.get(config), field.getType());
                } catch (Exception e) {
                    NaturallyTrimmed.LOGGER.error("Failed to serialize config field \"{}\". Saving as null.", field.getName(), e);
                    jsonWriter.nullValue();
                    continue;
                }

                this.gson.toJson(element, gsonWriter);
            } else if (field.isAnnotationPresent(SubConfig.class)) {
                assertPublicField(field);
                SubConfig subConfig = Objects.requireNonNull(field.getAnnotation(SubConfig.class));

                jsonWriter.name(annotationOrField(subConfig, field));
                jsonWriter.comment(subConfig.comment());

                jsonWriter.beginObject();
                recursivelySerialize(jsonWriter, gsonWriter, field.get(config));
                jsonWriter.endObject();
            }
        }
    }

    /** Asserts that the given field is public */
    private void assertPublicField(Field field) throws IllegalStateException {
        if (!Modifier.isPublic(field.getModifiers())) {
            throw new IllegalStateException("Config field " + field.getName() + " located in " + field.getDeclaringClass().getName() + " is not public.");
        }
    }

    private String annotationOrField(Entry entry, Field field) {
        return (entry.name().isEmpty()) ? field.getName() : entry.name();
    }

    private String annotationOrField(SubConfig subConfig, Field field) {
        return (subConfig.name().isEmpty()) ? field.getName() : subConfig.name();
    }
}
