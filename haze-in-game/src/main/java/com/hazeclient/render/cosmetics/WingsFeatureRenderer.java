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

public class WingsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public WingsFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
        
        // Simple wing model construction
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        
        ModelPartData right = modelPartData.addChild("right_wing", ModelPartBuilder.create().uv(0, 0).cuboid(-10.0f, -2.0f, 0.0f, 10.0f, 16.0f, 1.0f), ModelTransform.pivot(-1.0f, 2.0f, 2.0f));
        ModelPartData left = modelPartData.addChild("left_wing", ModelPartBuilder.create().uv(0, 0).cuboid(0.0f, -2.0f, 0.0f, 10.0f, 16.0f, 1.0f, true), ModelTransform.pivot(1.0f, 2.0f, 2.0f));
        
        ModelPart root = TexturedModelData.of(modelData, 64, 64).createModel();
        this.rightWing = root.getChild("right_wing");
        this.leftWing = root.getChild("left_wing");
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        CosmeticsManager.PlayerCosmetics cosmetics = CosmeticsManager.getCosmetics(player);
        if (cosmetics == null || cosmetics.wingsTexture == null) return;
        
        if (player.isInvisible()) return;

        matrices.push();
        
        // Inherit body rotation
        this.getContextModel().body.rotate(matrices);
        
        // Flap animation logic based on movement/time
        float flapSpeed = player.isOnGround() ? 0.1f : 0.4f;
        float flapAmount = player.isOnGround() ? 0.2f : 0.8f;
        
        float time = player.age + tickDelta;
        float flap = (float) (Math.sin(time * flapSpeed) * flapAmount);
        
        this.rightWing.yaw = flap - 0.5f;
        this.leftWing.yaw = -flap + 0.5f;
        
        this.rightWing.roll = 0.2f;
        this.leftWing.roll = -0.2f;

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(cosmetics.wingsTexture));
        
        this.rightWing.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        this.leftWing.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        
        matrices.pop();
    }
}
