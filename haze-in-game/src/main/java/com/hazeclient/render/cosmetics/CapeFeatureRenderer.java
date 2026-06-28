package com.hazeclient.render.cosmetics;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class CapeFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public CapeFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        CosmeticsManager.PlayerCosmetics cosmetics = CosmeticsManager.getCosmetics(player);
        if (cosmetics == null || cosmetics.capeTexture == null) return;
        
        // Don't render cape if player has an elytra equipped
        ItemStack chest = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST);
        if (chest.isOf(Items.ELYTRA)) return;

        if (player.isInvisible()) return;

        matrices.push();
        
        // Transform based on body rotation and sneaking
        matrices.translate(0.0, 0.0, 0.125);
        
        double d = MathHelper.lerp((double)tickDelta, player.prevCapeX, player.capeX) - MathHelper.lerp((double)tickDelta, player.prevX, player.getX());
        double e = MathHelper.lerp((double)tickDelta, player.prevCapeY, player.capeY) - MathHelper.lerp((double)tickDelta, player.prevY, player.getY());
        double m = MathHelper.lerp((double)tickDelta, player.prevCapeZ, player.capeZ) - MathHelper.lerp((double)tickDelta, player.prevZ, player.getZ());
        
        float n = player.prevBodyYaw + (player.bodyYaw - player.prevBodyYaw);
        
        double o = Math.sin(n * 0.017453292f);
        double p = -Math.cos(n * 0.017453292f);
        
        float q = (float)e * 10.0f;
        q = MathHelper.clamp(q, -6.0f, 32.0f);
        
        float r = (float)(d * o + m * p) * 100.0f;
        r = MathHelper.clamp(r, 0.0f, 150.0f);
        
        float s = (float)(d * p - m * o) * 100.0f;
        s = MathHelper.clamp(s, -20.0f, 20.0f);
        
        if (r < 0.0f) {
            r = 0.0f;
        }
        
        float t = MathHelper.lerp(tickDelta, player.prevStrideDistance, player.strideDistance);
        q += Math.sin(MathHelper.lerp(tickDelta, player.prevHorizontalSpeed, player.horizontalSpeed) * 6.0f) * 32.0f * t;
        
        if (player.isSneaking()) {
            q += 25.0f;
            matrices.translate(0.0, 0.15, 0.0);
        }
        
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0f + r / 2.0f + q));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(s / 2.0f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - s / 2.0f));
        
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(cosmetics.capeTexture));
        this.getContextModel().renderCape(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        
        matrices.pop();
    }
}
