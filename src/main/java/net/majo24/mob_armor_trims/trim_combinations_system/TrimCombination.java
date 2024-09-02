package net.majo24.mob_armor_trims.trim_combinations_system;

import java.util.List;

public record TrimCombination(String materialToApplyTo, CustomTrim helmetTrim, CustomTrim chestplateTrim, CustomTrim leggingsTrim, CustomTrim bootsTrim) {
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
        return List.of(this.bootsTrim, this.leggingsTrim, this.chestplateTrim, this.helmetTrim);
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