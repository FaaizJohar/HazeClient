package com.hazeclient.modules.settings;

/**
 * ARGB color setting. Renders as a color picker in ClickGUI.
 * Stored as a packed 0xAARRGGBB int.
 */
public class ColorSetting extends Setting<Integer> {

    private final boolean hasAlpha;

    public ColorSetting(String name, String description, int defaultColor) {
        this(name, description, defaultColor, true);
    }

    public ColorSetting(String name, String description, int defaultColor, boolean hasAlpha) {
        super(name, description, defaultColor);
        this.hasAlpha = hasAlpha;
    }

    public boolean hasAlpha() { return hasAlpha; }

    public int getRed()   { return (get() >> 16) & 0xFF; }
    public int getGreen() { return (get() >>  8) & 0xFF; }
    public int getBlue()  { return  get()        & 0xFF; }
    public int getAlpha() { return (get() >> 24) & 0xFF; }

    public float getRedF()   { return getRed()   / 255f; }
    public float getGreenF() { return getGreen() / 255f; }
    public float getBlueF()  { return getBlue()  / 255f; }
    public float getAlphaF() { return getAlpha() / 255f; }

    /** Sets from RGB components (alpha preserved from current value). */
    public void setRGB(int r, int g, int b) {
        set((getAlpha() << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF));
    }

    /** Sets from RGBA components. */
    public void setRGBA(int r, int g, int b, int a) {
        set(((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF));
    }

    /** Converts to HSB array [hue 0–360, saturation 0–1, brightness 0–1]. */
    public float[] toHSB() {
        float r = getRedF(), g = getGreenF(), b = getBlueF();
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        float hue = 0;
        if (delta != 0) {
            if (max == r)      hue = 60 * (((g - b) / delta) % 6);
            else if (max == g) hue = 60 * (((b - r) / delta) + 2);
            else               hue = 60 * (((r - g) / delta) + 4);
        }
        if (hue < 0) hue += 360;

        float saturation = (max == 0) ? 0 : delta / max;
        return new float[] { hue, saturation, max };
    }

    /** Sets from HSB values. */
    public void fromHSB(float h, float s, float brightness) {
        h = h % 360;
        if (h < 0) h += 360;
        float c = brightness * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = brightness - c;

        float r, g, b;
        if      (h < 60)  { r = c; g = x; b = 0; }
        else if (h < 120) { r = x; g = c; b = 0; }
        else if (h < 180) { r = 0; g = c; b = x; }
        else if (h < 240) { r = 0; g = x; b = c; }
        else if (h < 300) { r = x; g = 0; b = c; }
        else              { r = c; g = 0; b = x; }

        setRGBA((int)((r + m) * 255), (int)((g + m) * 255), (int)((b + m) * 255), getAlpha());
    }

    @Override public String getType() { return "color"; }

    @Override
    public Object serialize() {
        return String.format("#%08X", get());
    }

    @Override
    public void deserialize(Object raw) {
        if (raw instanceof Number n) {
            set(n.intValue());
        } else if (raw instanceof String s) {
            try {
                s = s.replace("#", "");
                if (s.length() == 6) {
                    set((int) (0xFF000000L | Long.parseLong(s, 16)));
                } else if (s.length() == 8) {
                    set((int) Long.parseLong(s, 16));
                }
            } catch (NumberFormatException ignored) {}
        }
    }
}
