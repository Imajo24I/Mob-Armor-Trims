package net.mob_armor_trims.majo24.config;

import net.mob_armor_trims.majo24.MobArmorTrims;

import java.util.List;

public class CustomTrim {
    private final String materialSNBT;
    private final String patternSNBT;

    public CustomTrim(String materialSNBT, String patternSNBT) {
        this.materialSNBT = materialSNBT;
        this.patternSNBT = patternSNBT;
    }

    public static CustomTrim fromStringified(List<String> stringifiedCustomTrim) {
        return new CustomTrim(stringifiedCustomTrim.get(0), stringifiedCustomTrim.get(1));
    }

    public String getMaterialSNBT() {
        return materialSNBT;
    }

    public String getPatternSNBT() {
        return patternSNBT;
    }
}
