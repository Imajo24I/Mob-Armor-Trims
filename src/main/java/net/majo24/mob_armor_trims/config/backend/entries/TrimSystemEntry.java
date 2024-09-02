package net.majo24.mob_armor_trims.config.backend.entries;

import net.majo24.mob_armor_trims.config.Config;

public class TrimSystemEntry extends ConfigEntry<Config.TrimSystems> {
    private Config.TrimSystems enabledSystem;

    public TrimSystemEntry(Config.TrimSystems defaultValue) {
        super(defaultValue);
        this.enabledSystem = defaultValue;
    }

    @Override
    public Config.TrimSystems getValue() {
        return this.enabledSystem;
    }

    @Override
    public void setValue(Config.TrimSystems value) {
        this.enabledSystem = value;
    }
}
