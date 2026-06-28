package com.cavrix.hazecore.module.impl;

import com.cavrix.hazecore.HazeCoreAgent;
import com.cavrix.hazecore.hud.elements.FpsHudElement;
import com.cavrix.hazecore.module.Module;
import com.cavrix.hazecore.module.ModuleCategory;

public class FpsModule extends Module {
    
    private FpsHudElement fpsHudElement;

    public FpsModule() {
        super("FPS", "Displays your frames per second on the screen", ModuleCategory.HUD);
    }

    @Override
    public void onInit() {
        this.fpsHudElement = new FpsHudElement();
        // Register the element to the render engine so it actually gets drawn
        HazeCoreAgent.getInstance().getRenderEngine().registerHudElement(fpsHudElement);
    }

    @Override
    public void onEnable() {
        // When the module is toggled on, show the HUD element
        if (this.fpsHudElement != null) {
            this.fpsHudElement.setVisible(true);
        }
    }

    @Override
    public void onDisable() {
        // When the module is toggled off, hide the HUD element
        if (this.fpsHudElement != null) {
            this.fpsHudElement.setVisible(false);
        }
    }
}
