package com.singlecheat.client.setting;

public class NumberSetting extends Setting<Double> {
    private final double min;
    private final double max;
    private final double step;

    public NumberSetting(String name, String description, double defaultValue, double min, double max, double step) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public void increment() { setValue(Math.min(max, value + step)); }
    public void decrement() { setValue(Math.max(min, value - step)); }
    public void setClamped(double v) { setValue(Math.max(min, Math.min(max, v))); }

    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getStep() { return step; }

    @Override public String getDisplayValue() {
        // Trim trailing zeros
        String s = String.format("%.2f", value);
        s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }
}
