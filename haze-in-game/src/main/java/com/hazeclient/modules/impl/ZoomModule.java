package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import com.hazeclient.modules.settings.FloatSetting;
import com.hazeclient.render.HazeAnimation;

public class ZoomModule extends Module {
    
    private final FloatSetting zoomFactor;
    private final HazeAnimation zoomAnim = new HazeAnimation(0f);

    public ZoomModule() {
        super("Zoom", "Zooms the camera in when enabled", Category.RENDER);
        zoomFactor = addSetting(new FloatSetting("Zoom Factor", "How much to zoom in", 4.0f, 1.5f, 10.0f, 0.5f));
    }

    public float getZoomFactor() {
        return zoomFactor.get();
    }

    public float getZoomAnim() {
        zoomAnim.springTo(isEnabled() ? 1f : 0f, 200f, 0.9f);
        zoomAnim.update();
        return zoomAnim.get();
    }
}
