package net.majo24.naturally_trimmed.config;

import net.majo24.naturally_trimmed.config.backend.annotations.Entry;
import net.majo24.naturally_trimmed.config.backend.annotations.SubConfig;
import net.majo24.naturally_trimmed.trim_combination.TrimCombination;

import java.util.ArrayList;
import java.util.List;

public class TrimMobsSubConfig {
    @Entry(name = "trim_system", comment = """
            Select the system of how to select, what trims to give mobs.
            - RANDOM_TRIMS: Randomly choosess the trim, while also considering the previous trim of the armor
            - CUSTOM_TRIM_COMBINATIONS: Chooses the trims for the armor from a list of trim combinations. You can manage the list of trim combinations yourself""")
    public TrimSystem trimSystem = TrimSystem.RANDOM_TRIMS;

    @Entry(name = "no_trims_chance", comment = "Chance of the mob having no trims at all")
    public int noTrimsChance = 25;

    @SubConfig(name = "random_trims", comment = "Settings for the Random Trims system.\nThese settings will only matter if the RANDOM_TRIMS system is enabled")
    public RandomTrimsSubConfig randomTrims = new RandomTrimsSubConfig();

    @Entry(name = "trim_combinations", comment = """
            List of custom trim combinations.
            
            A trim combination consists of a list of allowed armor materials and four trims.
            The trim combination will only be applied to armor that consists of one of the allowed armor materials.
            The four trims consist of a helmet trim, a chestplate trim, a leggings trim and a boots trim.
            You don't need to add the "_armor_trim_smithing_template" suffix of a pattern, it will be added automatically if its needed.
            
            To ease the configuration of trim combinations, theres a utils tab inside the config screen.
            You can use the buttons inside the tab to reload the config from the config file without having to restart the game and to validate the trim combinations.
            
            Example of a trim combination:
            {
                allowed_armor_materials: ["gold"],
                helmet_trim: {material: "diamond", pattern: "silence"},
                chestplate_trim: {material: "diamond", pattern: "silence"},
                leggings_trim: {material: "netherite_ingot", pattern: "vex"},
                boots_trim: {material: "netherite_ingot", pattern: "vex"}
            },
            """)
    public List<TrimCombination> trimCombinations = new ArrayList<>();

    public static class RandomTrimsSubConfig {
        @Entry(name = "trim_chance", comment = "Chance of each armor piece from a mob having an armor trim")
        public int trimChance = 75;

        @Entry(name = "similar_trim_chance", comment = "Chance of each armor piece having a similar armor trim as the previous armor piece")
        public int similarTrimChance = 75;
    }

    public enum TrimSystem {
        RANDOM_TRIMS,
        CUSTOM_TRIM_COMBINATIONS,
    }
}
