package com.hazeclient.modules.settings;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, String description, boolean defaultValue) {
        super(name, description, defaultValue);
    }

    @Override public String getType() { return "boolean"; }

    @Override
    public Object serialize() { return get(); }

    @Override
    public void deserialize(Object raw) {
        if (raw instanceof Boolean b) set(b);
        else if (raw instanceof String s) set(Boolean.parseBoolean(s));
    }
}
