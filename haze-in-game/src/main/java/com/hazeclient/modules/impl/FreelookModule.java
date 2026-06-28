package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import com.hazeclient.modules.settings.BooleanSetting;
import net.minecraft.client.MinecraftClient;

public class FreelookModule extends Module {
    
    private final BooleanSetting invertY;
    
    private float cameraYaw;
    private float cameraPitch;
    
    private int originalPerspective = 0; // 0: first person, 1: third person back, 2: third person front

    public FreelookModule() {
        super("Freelook", "Allows looking around without changing body direction", Category.RENDER);
        invertY = addSetting(new BooleanSetting("Invert Y", "Invert vertical look axis", false));
    }

    @Override
    protected void onEnable() {
        if (mc.player != null) {
            cameraYaw = mc.player.getYaw();
            cameraPitch = mc.player.getPitch();
            originalPerspective = mc.options.getPerspective().ordinal();
            // Force 3rd person back
            mc.options.setPerspective(net.minecraft.client.option.Perspective.THIRD_PERSON_BACK);
        }
    }

    @Override
    protected void onDisable() {
        if (mc.player != null) {
            // Restore perspective if we were the ones who changed it
            if (mc.options.getPerspective() == net.minecraft.client.option.Perspective.THIRD_PERSON_BACK) {
                mc.options.setPerspective(net.minecraft.client.option.Perspective.values()[originalPerspective]);
            }
        }
    }

    public void updateCamera(double cursorDeltaX, double cursorDeltaY) {
        float f = (float) cursorDeltaX * 0.15f;
        float g = (float) cursorDeltaY * 0.15f * (invertY.get() ? -1 : 1);
        
        cameraYaw += f;
        cameraPitch += g;
        
        // Clamp pitch
        if (cameraPitch > 90.0f) cameraPitch = 90.0f;
        if (cameraPitch < -90.0f) cameraPitch = -90.0f;
    }

    public float getCameraYaw() { return cameraYaw; }
    public float getCameraPitch() { return cameraPitch; }
}
