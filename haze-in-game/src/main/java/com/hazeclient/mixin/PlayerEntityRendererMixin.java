package com.hazeclient.mixin;

import com.hazeclient.render.cosmetics.CapeFeatureRenderer;
import com.hazeclient.render.cosmetics.HaloFeatureRenderer;
import com.hazeclient.render.cosmetics.WingsFeatureRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        this.addFeature(new CapeFeatureRenderer(this));
        this.addFeature(new WingsFeatureRenderer(this));
        this.addFeature(new HaloFeatureRenderer(this));
    }
}
