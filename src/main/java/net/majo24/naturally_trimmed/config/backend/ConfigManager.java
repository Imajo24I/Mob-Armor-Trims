package net.majo24.naturally_trimmed.config.backend;

import com.google.gson.*;
import net.majo24.naturally_trimmed.NaturallyTrimmed;
import net.majo24.naturally_trimmed.config.backend.annotations.Entry;
import net.majo24.naturally_trimmed.config.backend.annotations.SubConfig;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.JsonWriter;
import org.quiltmc.parsers.json.gson.GsonReader;
import org.quiltmc.parsers.json.gson.GsonWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
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
import java.util.regex.Pattern;

public class ConfigManager<T> {
    private T instance;
    private final T defaults;
    private final Path configPath;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Pattern.class, new PatternTypeAdapter())
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .serializeNulls().setPrettyPrinting()
            .create();

    public ConfigManager(Class<T> configClass, Path configPath) {
        this.configPath = configPath;
        this.defaults = createDefaultInstance(configClass);
        this.instance = defaults;
    }

    public T instance() {
        return instance;
    }

    public T defaults() {
        return defaults;
    }

    /**
     * Deserializes the config file and loads the instance
     */
    public void loadInstance() {
        NaturallyTrimmed.LOGGER.info("Loading Naturally Trimmed config from config file");
        if (!Files.exists(configPath)) {
            NaturallyTrimmed.LOGGER.info("Creating new Naturally Trimmed config file with default values.");
            saveInstance();
            return;
        }

        try (JsonReader jsonReader = JsonReader.json5(configPath)) {
            GsonReader gsonReader = new GsonReader(jsonReader);
            jsonReader.beginObject();
            recursivelyDeserialize(jsonReader, gsonReader, instance);
            jsonReader.endObject();

        } catch (Exception e) {
            NaturallyTrimmed.LOGGER.error("Failed to deserialize the Naturally Trimmed config file. Using the default config instead.", e);
            this.instance = defaults;
        }
    }

    private void recursivelyDeserialize(JsonReader jsonReader, GsonReader gsonReader, Object config) throws Exception {
        Map<String, Field> fieldMap = new HashMap<>();
        Arrays.stream(config.getClass().getDeclaredFields()).forEach(field -> {
            if (field.isAnnotationPresent(Entry.class)) {
                fieldMap.put(Objects.requireNonNull(field.getAnnotation(Entry.class)).name(), field);
            } else if (field.isAnnotationPresent(SubConfig.class)) {
                fieldMap.put(Objects.requireNonNull(field.getAnnotation(SubConfig.class)).name(), field);
            }
        });

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            Field field = fieldMap.get(name);

            if (field == null) {
                NaturallyTrimmed.LOGGER.warn("Found unknown config field \"{}\" while deserializing config file", name);
                jsonReader.skipValue();
                continue;
            }
            fieldMap.remove(name);
            ensureFieldIsPublic(field);

            if (field.isAnnotationPresent(Entry.class)) {
                JsonElement element = this.gson.fromJson(gsonReader, JsonElement.class);

                if (element.isJsonNull()) {
                    NaturallyTrimmed.LOGGER.warn("Found null value for config field {} while deserializing config file. Using default instead.", name);
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
    public void saveInstance() {
        NaturallyTrimmed.LOGGER.info("Saving Naturally Trimmed config to file");

        try (StringWriter stringWriter = new StringWriter()) {
            JsonWriter jsonWriter = JsonWriter.json5(stringWriter);
            GsonWriter gsonWriter = new GsonWriter(jsonWriter);
            jsonWriter.beginObject();

            recursivelySerialize(jsonWriter, gsonWriter, instance);

            jsonWriter.endObject();
            jsonWriter.flush();

            Files.writeString(this.configPath, stringWriter.toString(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (Exception e) {
            NaturallyTrimmed.LOGGER.error("Failed to serialize and save Naturally Trimmed config to config file", e);
        }
    }

    private void recursivelySerialize(JsonWriter jsonWriter, GsonWriter gsonWriter, Object config) throws IOException, IllegalAccessException {
        for (Field field : config.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Entry.class)) {
                ensureFieldIsPublic(field);

                Entry entry = field.getAnnotation(Entry.class);

                jsonWriter.name(Objects.requireNonNull(entry).name());
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
                ensureFieldIsPublic(field);
                SubConfig subConfig = Objects.requireNonNull(field.getAnnotation(SubConfig.class));

                jsonWriter.name(subConfig.name());
                jsonWriter.comment(subConfig.comment());

                jsonWriter.beginObject();
                recursivelySerialize(jsonWriter, gsonWriter, field.get(config));
                jsonWriter.endObject();
            }
        }
    }

    private void ensureFieldIsPublic(Field field) {
        if (!Modifier.isPublic(field.getModifiers())) {
            throw new IllegalStateException("Config field " + field.getName() + " located in " + field.getDeclaringClass().getName() + " is not public.");
        }
    }

    private T createDefaultInstance(Class<T> configClass) {
        Constructor<T> noArgsConstructor;
        try {
            noArgsConstructor = configClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new ClassFormatError("Failed to find no-args constructor for config class " + configClass.getName() + "\n" + e);
        }

        try {
            return noArgsConstructor.newInstance();
        } catch (Exception e) {
            throw new ClassFormatError("Failed to load default config for class " + noArgsConstructor.getDeclaringClass().getName() + "\n" + e);
        }
    }

    public static class PatternTypeAdapter implements JsonSerializer<Pattern>, JsonDeserializer<Pattern> {
        @Override
        public JsonElement serialize(Pattern src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.pattern());
        }

        @Override
        public Pattern deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Pattern.compile(json.getAsString());
        }
    }
}
