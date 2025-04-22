package net.majo24.mob_armor_trims.config;

import net.majo24.mob_armor_trims.config.backend.annotations.Entry;
import net.majo24.mob_armor_trims.config.backend.annotations.SubConfig;
import net.majo24.mob_armor_trims.trim_combinations_system.CustomTrim;
import net.majo24.mob_armor_trims.trim_combinations_system.TrimCombination;

import java.util.List;
import java.util.regex.Pattern;

public class TrimMobsSubConfig {
    @Entry(name = "trim_system", comment = """
            Select the system of how to select, what trims to give mobs.
            - RANDOM_TRIMS: Randomly choose the trim, but also take the previous trim highly into account.
            - CUSTOM_TRIMS: Choose the trim from a list of custom trims. You can manage the trims yourself""")
    public TrimSystem trimSystem = TrimSystem.RANDOM_TRIMS;

    @Entry(name = "no_trims_chance", comment = "Chance of the mob having no trims at all")
    public int noTrimsChance = 25;

    @SubConfig(name = "random_trims", comment = "Settings for the Random Trims system.\nThese settings will only matter if the RANDOM_TRIMS system is enabled")
    public RandomTrimsSubConfig randomTrims = new RandomTrimsSubConfig();

    @Entry(name = "trim_combinations", comment = "List of trim combinations")
    public List<TrimCombination> trimCombinations = List.of(
            new TrimCombination("gold", new CustomTrim("gold", "silence"), new CustomTrim("netherite", "silence"), new CustomTrim("gold", "silence"), new CustomTrim("netherite", "silence"))
    );

    public static class RandomTrimsSubConfig {
        @Entry(name = "trim_chance", comment = "Chance of each armor piece from a mob having an armor trim")
        public int trimChance = 75;

        @Entry(name = "similar_trim_chance", comment = "Chance of each armor piece having a similar armor trim as the previous armor piece")
        public int similarTrimChance = 75;

        @Entry(name = "blacklist", comment = "Blacklist for trim patterns.\nThis uses regex. This means, if you want to check for a specific pattern, you need to use \"^[pattern$]\" instead of just \"[pattern]\".\nBy default, all patterns from the Trimmable Tools mod are blacklisted, since they only work for tools and not armor")
        public List<Pattern> blacklist = List.of(Pattern.compile("^tooltrims:.*"));
    }

    public enum TrimSystem {
        RANDOM_TRIMS,
        CUSTOM_TRIM_COMBINATIONS,
    }
}
