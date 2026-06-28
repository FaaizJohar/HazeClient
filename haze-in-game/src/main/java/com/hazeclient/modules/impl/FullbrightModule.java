package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;

public class FullbrightModule extends Module {

    public FullbrightModule() {
        super("Fullbright", "Renders the world at maximum brightness", Category.RENDER);
    }
    
    @Override
    protected void onEnable() {
        if (mc.options != null) {
            mc.options.getGamma().setValue(100.0);
        }
    }
    
    @Override
    protected void onDisable() {
        if (mc.options != null) {
            mc.options.getGamma().setValue(1.0); // Reset to default bright
        }
    }
}
