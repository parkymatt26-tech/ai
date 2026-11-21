package com.singlecheat.client.setting;

import java.util.List;

public class ModeSetting extends Setting<String> {
    private final List<String> modes;
    private int index;

    public ModeSetting(String name, String description, String defaultValue, List<String> modes) {
        super(name, description, defaultValue);
        this.modes = modes;
        this.index = modes.indexOf(defaultValue);
        if (index < 0) index = 0;
    }

    public void cycle() {
        index = (index + 1) % modes.size();
        this.value = modes.get(index);
    }

    public void setMode(String mode) {
        int idx = modes.indexOf(mode);
        if (idx < 0) return;
        this.index = idx;
        this.value = mode;
    }

    public List<String> getModes() { return modes; }
    public int getIndex() { return index; }

    @Override public String getDisplayValue() { return value; }
}
