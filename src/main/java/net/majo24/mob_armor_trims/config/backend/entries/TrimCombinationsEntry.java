package net.majo24.mob_armor_trims.config.backend.entries;

import net.majo24.mob_armor_trims.trim_combinations_system.TrimCombination;

import java.util.ArrayList;
import java.util.List;

public class TrimCombinationsEntry extends ConfigEntry<List<List<List<String>>>> {
    private List<TrimCombination> trimCombinations = new ArrayList<>();

    public TrimCombinationsEntry(List<List<List<String>>> defaultValue) {
        super(defaultValue);
    }

    public List<TrimCombination> getTrimCombinations() {
        return trimCombinations;
    }

    public void setTrimCombinations(List<TrimCombination> trimCombinations) {
        this.trimCombinations = trimCombinations;
    }

    @Override
    public List<List<List<String>>> getValue() {
        return this.trimCombinations.stream().map(TrimCombination::toStringList).toList();
    }

    @Override
    public void setValue(List<List<List<String>>> value) {
        this.trimCombinations = value.stream().map(TrimCombination::trimCombinationFromList).toList();
    }

    public List<TrimCombination> getDefaultTrimCombinations() {
        return new ArrayList<>();
    }
}
