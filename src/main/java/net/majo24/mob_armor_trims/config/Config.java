package net.majo24.mob_armor_trims.config;

public class Config {
    private int trimChance = 50;

    public Config(int trimChance) {
        this.trimChance = trimChance;
    }

    public int getTrimChance() {
        return trimChance;
    }

    public void setTrimChance(int trimChance) {
        this.trimChance = trimChance;
    }
}
