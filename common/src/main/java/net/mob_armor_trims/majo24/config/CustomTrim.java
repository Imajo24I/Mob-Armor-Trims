package net.mob_armor_trims.majo24.config;

import net.mob_armor_trims.majo24.MobArmorTrims;

public class CustomTrim {
    private final String materialSNBT;
    private final String patternSNBT;

    public CustomTrim(String materialSNBT, String patternSNBT) {
        this.materialSNBT = materialSNBT;
        this.patternSNBT = patternSNBT;
    }

    public static CustomTrim fromStringified(String stringifiedCustomTrim) {
        String[] customTrim = stringifiedCustomTrim.replace(" ", "").split(";");
        try {
            return new CustomTrim(customTrim[0], customTrim[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            MobArmorTrims.LOGGER.warn("Could not parse custom trim: {}. Please ensure its a valid Trim Combination.", stringifiedCustomTrim);
            return null;
        }
    }

    public String getMaterialSNBT() {
        return materialSNBT;
    }

    public String getPatternSNBT() {
        return patternSNBT;
    }
}
