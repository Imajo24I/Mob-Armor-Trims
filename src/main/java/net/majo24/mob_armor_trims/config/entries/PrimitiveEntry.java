package net.majo24.mob_armor_trims.config.entries;

/**
 * This is the ConfigEntry class for primitive types.
 * Primitive Types are types that NightConfig can save and load by default.
 * So for example, int, string, double, Lists that contain primitive types and so on
 */
public class PrimitiveEntry<T> extends ConfigEntry<T> {
    private T value;

    public PrimitiveEntry(T defaultValue) {
        super(defaultValue);
        this.value = defaultValue;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }
}
