package com.hazeclient.modules.settings;

/**
 * Integer setting with min/max bounds. Renders as a slider in ClickGUI.
 */
public class IntSetting extends Setting<Integer> {

    private final int min;
    private final int max;
    private final int step;

    public IntSetting(String name, String description, int defaultValue, int min, int max) {
        this(name, description, defaultValue, min, max, 1);
    }

    public IntSetting(String name, String description, int defaultValue, int min, int max, int step) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    @Override
    public void set(Integer newValue) {
        if (newValue != null) {
            super.set(Math.max(min, Math.min(max, newValue)));
        }
    }

    public int getMin() { return min; }
    public int getMax() { return max; }
    public int getStep() { return step; }

    /** Returns 0.0–1.0 normalized position within [min, max]. */
    public float getNormalized() {
        return (max == min) ? 0f : (float) (get() - min) / (max - min);
    }

    /** Sets from a 0.0–1.0 normalized value. */
    public void setNormalized(float normalized) {
        int val = min + Math.round(normalized * (max - min));
        // Snap to step
        val = min + Math.round((float) (val - min) / step) * step;
        set(val);
    }

    @Override public String getType() { return "int"; }

    @Override
    public Object serialize() { return get(); }

    @Override
    public void deserialize(Object raw) {
        if (raw instanceof Number n) set(n.intValue());
        else if (raw instanceof String s) {
            try { set(Integer.parseInt(s)); } catch (NumberFormatException ignored) {}
        }
    }
}
