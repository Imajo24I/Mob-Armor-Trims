package net.majo24.mob_armor_trims.config.entries;

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
