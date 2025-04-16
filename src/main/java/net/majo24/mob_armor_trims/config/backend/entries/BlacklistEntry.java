package net.majo24.mob_armor_trims.config.backend.entries;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BlacklistEntry extends ConfigEntry<List<String>> {
    private List<Pattern> patterns = new ArrayList<>();

    public BlacklistEntry(List<String> defaultValue) {
        super(defaultValue);
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<Pattern> patterns) {
        this.patterns = patterns;
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
        this.patterns = value.stream().map(Pattern::compile).toList();
    }
}
