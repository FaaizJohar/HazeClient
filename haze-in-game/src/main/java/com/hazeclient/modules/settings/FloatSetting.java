package com.hazeclient.modules.settings;

/**
 * Float setting with min/max bounds and step precision. Renders as a slider in ClickGUI.
 */
public class FloatSetting extends Setting<Float> {

    private final float min;
    private final float max;
    private final float step;

    public FloatSetting(String name, String description, float defaultValue, float min, float max) {
        this(name, description, defaultValue, min, max, 0.01f);
    }

    public FloatSetting(String name, String description, float defaultValue, float min, float max, float step) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    @Override
    public void set(Float newValue) {
        if (newValue != null) {
            // Snap to step
            float val = Math.max(min, Math.min(max, newValue));
            val = Math.round(val / step) * step;
            super.set(val);
        }
    }

    public float getMin() { return min; }
    public float getMax() { return max; }
    public float getStep() { return step; }

    /** Returns 0.0–1.0 normalized position within [min, max]. */
    public float getNormalized() {
        return (max == min) ? 0f : (get() - min) / (max - min);
    }

    /** Sets from a 0.0–1.0 normalized value. */
    public void setNormalized(float normalized) {
        set(min + normalized * (max - min));
    }

    @Override public String getType() { return "float"; }

    @Override
    public Object serialize() { return get(); }

    @Override
    public void deserialize(Object raw) {
        if (raw instanceof Number n) set(n.floatValue());
        else if (raw instanceof String s) {
            try { set(Float.parseFloat(s)); } catch (NumberFormatException ignored) {}
        }
    }
}
