package net.majo24.mob_armor_trims.config.entries;

public abstract class ConfigEntry<T> {
    private final T defaultValue;

    protected ConfigEntry(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public abstract T getValue();

    public abstract void setValue(T value);

    public T getDefaultValue() {
        return defaultValue;
    }
}
