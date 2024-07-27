package net.majo24.mob_armor_trims.config.entries;

public abstract class ConfigEntry<T> {
    private final T defaultValue;

    /**
     * @param defaultValue the default value in a primitive type
     */
    protected ConfigEntry(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return the value in a primitive type
     */
    public abstract T getValue();

    /**
     * @param value the value in a primitive type
     */
    public abstract void setValue(T value);

    /**
     * @return the default value in a primitive type
     */
    public T getDefaultValue() {
        return defaultValue;
    }
}
