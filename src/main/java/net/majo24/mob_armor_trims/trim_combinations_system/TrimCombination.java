package net.majo24.mob_armor_trims.trim_combinations_system;

import java.util.ArrayList;
import java.util.List;

public record TrimCombination(String materialToApplyTo, CustomTrim helmetTrim, CustomTrim chestplateTrim,
                              CustomTrim leggingsTrim, CustomTrim bootsTrim) {
    public List<List<String>> toStringList() {
        return List.of(List.of(this.materialToApplyTo),
                this.bootsTrim.toList(),
                this.leggingsTrim.toList(),
                this.chestplateTrim.toList(),
                this.helmetTrim.toList());
    }

    /**
     * @return A list of all custom trims from this trim combination
     */
    public List<CustomTrim> trims() {
        return new ArrayList<>(List.of(this.helmetTrim, this.chestplateTrim, this.leggingsTrim, this.bootsTrim));
    }

    /**
     * Replaces the custom trim at the given index
     *
     * @param trim  The new custom trim
     * @param index Index of the replaced trim (0: boots, 1: leggings, 2: chestplate, 3: helmet)
     * @return A new TrimCombination
     */
    public TrimCombination withTrim(CustomTrim trim, int index) {
        List<CustomTrim> trims = this.trims();
        trims.set(index, trim);
        return new TrimCombination(this.materialToApplyTo, trims.get(0), trims.get(1), trims.get(2), trims.get(3));
    }

    /**
     * Replaces the material to apply the trims to
     *
     * @param materialToApplyTo The material to apply the trims to
     * @return A new TrimCombination
     */
    public TrimCombination withMaterialToApplyTo(String materialToApplyTo) {
        return new TrimCombination(materialToApplyTo, this.bootsTrim, this.leggingsTrim, this.chestplateTrim, this.helmetTrim);
    }

    /**
     * @param trimCombination The trim combination in the form of a list
     * @return The trim combination
     */
    public static TrimCombination trimCombinationFromList(List<List<String>> trimCombination) {
        return new TrimCombination(trimCombination.get(0).get(0),
                CustomTrim.fromList(trimCombination.get(1)),
                CustomTrim.fromList(trimCombination.get(2)),
                CustomTrim.fromList(trimCombination.get(3)),
                CustomTrim.fromList(trimCombination.get(4)));
    }
}