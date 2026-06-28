package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import com.hazeclient.modules.settings.ColorSetting;
import com.hazeclient.modules.settings.FloatSetting;

public class BlockOverlayModule extends Module {

    private final ColorSetting outlineColor;
    private final ColorSetting fillColor;
    private final FloatSetting lineWidth;

    public BlockOverlayModule() {
        super("Block Overlay", "Customizes the block selection outline", Category.RENDER);
        outlineColor = addSetting(new ColorSetting("Outline Color", "Color of the block outline", 0xFFFFFFFF));
        fillColor = addSetting(new ColorSetting("Fill Color", "Color of the block face fill", 0x33FFFFFF));
        lineWidth = addSetting(new FloatSetting("Line Width", "Thickness of the outline", 2.0f, 0.5f, 5.0f, 0.5f));
    }

    public int getOutlineColor() { return outlineColor.get(); }
    public int getFillColor() { return fillColor.get(); }
    public float getLineWidth() { return lineWidth.get(); }
}
