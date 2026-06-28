package com.hazeclient.mixin;

import com.hazeclient.modules.ModuleRegistry;
import com.hazeclient.modules.impl.FreelookModule;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mouse.class)
public class MouseMixin {

    @Redirect(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    private void redirectChangeLookDirection(ClientPlayerEntity player, double cursorDeltaX, double cursorDeltaY) {
        FreelookModule freelook = ModuleRegistry.getModule(FreelookModule.class);
        if (freelook != null && freelook.isEnabled()) {
            freelook.updateCamera(cursorDeltaX, cursorDeltaY);
        } else {
            player.changeLookDirection(cursorDeltaX, cursorDeltaY);
        }
    }
}
