package com.hazeclient.render.cosmetics;

import net.minecraft.client.model.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class HaloFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    private final ModelPart halo;

    public HaloFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
        
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        
        // Create a simple torus approximation or a flat disc for the halo
        modelPartData.addChild("halo", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -1.0f, -4.0f, 8.0f, 1.0f, 8.0f), ModelTransform.pivot(0.0f, -10.0f, 0.0f));
        
        ModelPart root = TexturedModelData.of(modelData, 32, 32).createModel();
        this.halo = root.getChild("halo");
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        CosmeticsManager.PlayerCosmetics cosmetics = CosmeticsManager.getCosmetics(player);
        if (cosmetics == null || !cosmetics.hasHalo) return;
        
        if (player.isInvisible()) return;

        matrices.push();
        
        // Inherit body/head position, but we just need body for the base anchor, then bob it
        this.getContextModel().head.rotate(matrices);
        
        float time = player.age + tickDelta;
        
        // Bobbing up and down
        matrices.translate(0.0, Math.sin(time * 0.1f) * 0.05f - 0.2f, 0.0);
        
        // Slowly rotate
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(time * 2.0f));
        
        // Color tinting using a translucent white texture (since we don't have one, we assume we use a blank one)
        // For actual rendering, we should pass the color
        float r = ((cosmetics.haloColor >> 16) & 0xFF) / 255.0f;
        float g = ((cosmetics.haloColor >> 8) & 0xFF) / 255.0f;
        float b = (cosmetics.haloColor & 0xFF) / 255.0f;

        // Uses a generic glowing texture or white blank
        Identifier haloTex = new Identifier("hazeclient", "textures/cosmetics/halo.png");
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(haloTex));
        
        this.halo.render(matrices, vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV, r, g, b, 0.8f);
        
        matrices.pop();
    }
}
