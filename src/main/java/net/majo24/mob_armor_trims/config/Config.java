package net.majo24.mob_armor_trims.config;

import net.majo24.mob_armor_trims.config.annotations.Entry;
import net.majo24.mob_armor_trims.config.annotations.SubConfig;
import net.majo24.mob_armor_trims.config.custom_trim_combinations.CustomTrim;
import net.majo24.mob_armor_trims.config.custom_trim_combinations.TrimCombination;
import net.majo24.mob_armor_trims.config.entries.PrimitiveEntry;
import net.majo24.mob_armor_trims.config.entries.TrimCombinationsEntry;
import net.majo24.mob_armor_trims.config.entries.TrimSystemEntry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Config {
    private final Map<List<String>, ArmorTrim> cachedCustomTrims = new HashMap<>();

    /**
     * @param requiredMaterial The material the trim combination has to match
     * @return A random trim combination that matches the given required material. Null if no trim combination matches.
     */
    @Nullable
    public TrimCombination getRandomTrimCombination(String requiredMaterial) {
        List<TrimCombination> trimCombinations = this.customTrimCombinations.trimCombinations.getTrimCombinations();
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

    @SubConfig(name = "general", description = "General Settings for the mod")
    public final GeneralSubConfig general = new GeneralSubConfig();

    @SubConfig(name = "random_trims", description = "Settings for the Random Trims system.\nThese settings will only make a difference, if the RANDOM_TRIMS system is enabled")
    public final RandomTrimsSubConfig randomTrims = new RandomTrimsSubConfig();

    @SubConfig(name = "trim_combinations", description = "Settings for the Custom Trim Combinations system.\nThese settings will only make a difference, if the CUSTOM_TRIM_COMBINATIONS system is enabled")
    public final TrimCombinationsSubConfig customTrimCombinations = new TrimCombinationsSubConfig();

    @SubConfig(name = "stacked_trims", description = "Settings for the Stacked Armor Trims Mod Compatibility.\\nThese settings will only make a difference, if the STACKED_TRIMS system is enabled and the stacked armor trims mod is used")
    public final StackedTrimsSubConfig stackedTrims = new StackedTrimsSubConfig();

    public static final class GeneralSubConfig {
        @Entry(name = "enabled_system", description = """
                Select the System of how to select, what trims to give mobs.
                - RANDOM_TRIMS: Randomly choose the trim, but also take the previous trim highly into account.
                - CUSTOM_TRIMS: Choose the trim from a list of custom trims. You can manage the trims yourself""")
        public final TrimSystemEntry enabledSystem = new TrimSystemEntry(TrimSystems.RANDOM_TRIMS);

        @Entry(name = "no_trims_chance", description = "Chance of the mob having no trims at all")
        public final PrimitiveEntry<Integer> noTrimsChance = new PrimitiveEntry<>(25);
    }

    public static final class RandomTrimsSubConfig {
        @Entry(name = "trim_chance", description = "Chance of each armor piece from a mob having an armor trim")
        public final PrimitiveEntry<Integer> trimChance = new PrimitiveEntry<>(50);

        @Entry(name = "similar_trim_chance", description = "Chance of each armor piece having a similar armor trim as the previous armor piece")
        public final PrimitiveEntry<Integer> similarTrimChance = new PrimitiveEntry<>(75);
    }

    public static final class TrimCombinationsSubConfig {
        @Entry(name = "controllers", description = """
                The list of custom trim combinations.
                
                To create a new trim combination, add a new list with with 5 lists inside.
                Then for the first inner list, add a String. In the rest of the inner lists, add 2 Strings.
                It should look somewhat like this: [[""], ["", ""], ["", ""], ["", ""], ["", ""]]
                Make sure to have the outer list separated with a comma from other trim combinations.
                
                In the first lists String, enter the Armor Material, the trim combination should be applied on.
                For example: ["gold"]
                
                For the rest of the lists, in the first String, enter a valid Trim Material.
                In the second String, enter a valid Trim Pattern
                To not have to specify the whole trim pattern, you can leave out the "_armor_trim_smithing_template" part of the pattern, as it is the same for every pattern.
                For example: ["amethyst_shard", "silence"]""")
        public final TrimCombinationsEntry trimCombinations = new TrimCombinationsEntry(new ArrayList<>());
    }

    public static final class StackedTrimsSubConfig {
        @Entry(name = "stacked_trim_chance", description = "Chance of each armor piece having an additional armor trim on ")
        public final PrimitiveEntry<Integer> stackedTrimChance = new PrimitiveEntry<>(10);

        @Entry(name = "max_stacked_trims", description = "The maximum amount of armor trims that can be stacked on each other")
        public final PrimitiveEntry<Integer> maxStackedTrims = new PrimitiveEntry<>(3);
    }

    public enum TrimSystems {
        RANDOM_TRIMS,
        CUSTOM_TRIM_COMBINATIONS,
        NONE,
    }
}
