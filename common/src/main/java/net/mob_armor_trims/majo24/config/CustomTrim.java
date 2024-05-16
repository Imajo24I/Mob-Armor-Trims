package net.mob_armor_trims.majo24.config;

public class CustomTrim {
    private final String materialSNBT;
    private final String patternSNBT;

    public CustomTrim(String materialSNBT, String patternSNBT) {
        this.materialSNBT = materialSNBT;
        this.patternSNBT = patternSNBT;
    }

    public static CustomTrim fromStringified(String stringifiedCustomTrim) {
        return new CustomTrim(stringifiedCustomTrim.split(" ")[0], stringifiedCustomTrim.split(" ")[1]);
    }

    public String getMaterialSNBT() {
        return materialSNBT;
    }

    public String getPatternSNBT() {
        return patternSNBT;
    }
}
