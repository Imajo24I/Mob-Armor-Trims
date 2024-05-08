package net.mob_armor_trims.majo24.config;

public class Config {
    private int trimChance;
    private int stackedTrimChance;
    private int maxStackedTrims;

    public Config(int trimChance, int stackedTrimChance, int maxStackedTrims) {
        this.trimChance = trimChance;
        this.stackedTrimChance = stackedTrimChance;
        this.maxStackedTrims = maxStackedTrims;
    }

    public int getTrimChance() {
        return trimChance;
    }

    public void setTrimChance(int trimChance) {
        this.trimChance = trimChance;
    }

    public int getStackedTrimChance() {
        return stackedTrimChance;
    }

    public void setStackedTrimChance(int stackedTrimChance) {
        this.stackedTrimChance = stackedTrimChance;
    }

    public int getMaxStackedTrims() {
        return maxStackedTrims;
    }

    public void setMaxStackedTrims(int maxStackedTrims) {
        this.maxStackedTrims = maxStackedTrims;
    }
}