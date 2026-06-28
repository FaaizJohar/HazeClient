package com.hazeclient.modules.settings;

/**
 * String setting — renders as a text field in ClickGUI.
 */
public class StringSetting extends Setting<String> {

    private final int maxLength;

    public StringSetting(String name, String description, String defaultValue) {
        this(name, description, defaultValue, 256);
    }

    public StringSetting(String name, String description, String defaultValue, int maxLength) {
        super(name, description, defaultValue);
        this.maxLength = maxLength;
    }

    @Override
    public void set(String newValue) {
        if (newValue != null && newValue.length() > maxLength) {
            newValue = newValue.substring(0, maxLength);
        }
        super.set(newValue);
    }

    public int getMaxLength() { return maxLength; }

    @Override public String getType() { return "string"; }

    @Override
    public Object serialize() { return get(); }

    @Override
    public void deserialize(Object raw) {
        if (raw instanceof String s) set(s);
    }
}
