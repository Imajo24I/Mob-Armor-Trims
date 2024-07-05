package net.majo24.mob_armor_trims.config.custom_trim_combinations;

import java.util.List;

public record TrimCombination(String materialToApplyTo, CustomTrim helmetTrim, CustomTrim chestplateTrim, CustomTrim leggingsTrim, CustomTrim bootsTrim) {
    public List<List<String>> toStringList() {
        return List.of(List.of(this.materialToApplyTo),
                this.bootsTrim.toStringList(),
                this.leggingsTrim.toStringList(),
                this.chestplateTrim.toStringList(),
                this.helmetTrim.toStringList());
    }

    public List<CustomTrim> trims() {
        return List.of(this.bootsTrim, this.leggingsTrim, this.chestplateTrim, this.helmetTrim);
    }

    public static List<TrimCombination> trimCombinationsFromStringList(List<List<List<String>>> trimCombinationsAsStringList) {
        return trimCombinationsAsStringList.stream().map(TrimCombination::trimCombinationFromStringList).toList();
    }

    public static TrimCombination trimCombinationFromStringList(List<List<String>> trimCombination) {
        return new TrimCombination(trimCombination.get(0).get(0),
                CustomTrim.fromList(trimCombination.get(1)),
                CustomTrim.fromList(trimCombination.get(2)),
                CustomTrim.fromList(trimCombination.get(3)),
                CustomTrim.fromList(trimCombination.get(4)));
    }
}