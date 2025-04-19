package net.majo24.mob_armor_trims.config.backend.entries;

import net.majo24.mob_armor_trims.MobArmorTrims;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class BlacklistEntry extends ConfigEntry<List<String>> {
    private List<Pattern> patterns = new ArrayList<>();

    public BlacklistEntry(List<String> defaultValue) {
        super(defaultValue);
        setValue(defaultValue);
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    /**
     * @return the value in a primitive type
     */
    @Override
    public List<String> getValue() {
        return patterns.stream().map(Pattern::pattern).toList();
    }

    /**
     * @param value the value in a primitive type
     */
    @Override
    public void setValue(List<String> value) {
        this.patterns = value.stream().map(pattern -> {
            try {
                return Pattern.compile(pattern);
            } catch (Exception e) {
                MobArmorTrims.LOGGER.error("Failed to parse regex pattern \"{}\". Please ensure this is a valid pattern. Skipping this pattern for now.", pattern, e);
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }
}
