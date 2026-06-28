package com.hazeclient.modules.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Type-safe module setting.
 *
 * Each module declares its configurable properties as typed Setting instances.
 * The ClickGUI auto-generates appropriate widgets (toggle, slider, dropdown, etc.)
 * based on the concrete subclass.
 *
 * @param <T> the value type
 */
public abstract class Setting<T> {

    private final String name;
    private final String description;
    private T value;
    private final T defaultValue;
    private final List<Consumer<T>> listeners = new ArrayList<>(2);

    protected Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    // ─── Core API ───────────────────────────────────────────────────────

    public String getName() { return name; }
    public String getDescription() { return description; }

    public T get() { return value; }

    public void set(T newValue) {
        if (newValue != null && !newValue.equals(this.value)) {
            this.value = newValue;
            for (Consumer<T> listener : listeners) {
                listener.accept(newValue);
            }
        }
    }

    public T getDefault() { return defaultValue; }

    public void reset() { set(defaultValue); }

    public void onChange(Consumer<T> listener) {
        listeners.add(listener);
    }

    // ─── Serialization (overridden by subtypes for type safety) ─────────

    /** Serialize to a JSON-compatible object (String, Number, Boolean). */
    public abstract Object serialize();

    /** Deserialize from a JSON-compatible object. */
    public abstract void deserialize(Object raw);

    /** Returns a string identifier for the setting type (for GUI widget selection). */
    public abstract String getType();
}
