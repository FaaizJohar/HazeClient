package com.hazeclient.modules.settings;

/**
 * Enum setting — renders as a dropdown/cycle button in ClickGUI.
 *
 * @param <E> any Enum type
 */
public class EnumSetting<E extends Enum<E>> extends Setting<E> {

    private final E[] values;

    public EnumSetting(String name, String description, E defaultValue) {
        super(name, description, defaultValue);
        this.values = defaultValue.getDeclaringClass().getEnumConstants();
    }

    /** All possible values. */
    public E[] getValues() { return values; }

    /** Cycle to next value (wraps around). */
    public void cycle() {
        int idx = (get().ordinal() + 1) % values.length;
        set(values[idx]);
    }

    /** Cycle backwards. */
    public void cycleBack() {
        int idx = (get().ordinal() - 1 + values.length) % values.length;
        set(values[idx]);
    }

    /** Current index in the enum's values array. */
    public int getIndex() { return get().ordinal(); }

    /** Set by index. */
    public void setIndex(int idx) {
        if (idx >= 0 && idx < values.length) {
            set(values[idx]);
        }
    }

    @Override public String getType() { return "enum"; }

    @Override
    public Object serialize() { return get().name(); }

    @Override
    public void deserialize(Object raw) {
        if (raw instanceof String s) {
            for (E val : values) {
                if (val.name().equalsIgnoreCase(s)) {
                    set(val);
                    return;
                }
            }
        }
    }
}
