package com.singlecheat.client.setting;

public abstract class Setting<T> {
    protected final String name;
    protected final String description;
    protected T value;
    protected final T defaultValue;

    public Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public T getValue() { return value; }
    public void setValue(T value) { this.value = value; }
    public T getDefaultValue() { return defaultValue; }
    public abstract String getDisplayValue();
}
