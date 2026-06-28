package com.hazeclient.mixin;

import com.hazeclient.modules.ModuleRegistry;
import com.hazeclient.modules.impl.FreelookModule;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow protected abstract void setRotation(float yaw, float pitch);
    @Shadow private float pitch;
    @Shadow private float yaw;

    @Inject(method = "update", at = @At("TAIL"))
    private void onUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        FreelookModule freelook = ModuleRegistry.getModule(FreelookModule.class);
        if (freelook != null && freelook.isEnabled() && thirdPerson) {
            // Override the rotation with the Freelook camera rotation
            this.setRotation(freelook.getCameraYaw(), freelook.getCameraPitch());
        }
    }
}
