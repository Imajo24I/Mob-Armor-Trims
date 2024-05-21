package net.mob_armor_trims.majo24.config;

import java.util.List;

public record CustomTrim(String materialSNBT, String patternSNBT) {

    public static CustomTrim fromStringified(List<String> stringifiedCustomTrim) {
        return new CustomTrim(stringifiedCustomTrim.get(0), stringifiedCustomTrim.get(1));
    }
}
