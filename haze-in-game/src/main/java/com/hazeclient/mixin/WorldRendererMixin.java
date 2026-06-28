package com.hazeclient.mixin;

import com.hazeclient.modules.ModuleRegistry;
import com.hazeclient.modules.impl.BlockOverlayModule;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
    private void onDrawBlockOutline(net.minecraft.client.util.math.MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, net.minecraft.block.BlockState state, CallbackInfo ci) {
        BlockOverlayModule module = ModuleRegistry.getModule(BlockOverlayModule.class);
        if (module != null && module.isEnabled()) {
            ci.cancel();

            VoxelShape shape = state.getOutlineShape(entity.getWorld(), pos, net.minecraft.block.ShapeContext.of(entity));
            if (shape.isEmpty()) return;

            double x = pos.getX() - cameraX;
            double y = pos.getY() - cameraY;
            double z = pos.getZ() - cameraZ;

            matrices.push();
            matrices.translate(x, y, z);

            int outlineColor = module.getOutlineColor();
            int fillColor = module.getFillColor();
            float lineWidth = module.getLineWidth();

            float oA = ((outlineColor >> 24) & 0xFF) / 255f;
            float oR = ((outlineColor >> 16) & 0xFF) / 255f;
            float oG = ((outlineColor >> 8) & 0xFF) / 255f;
            float oB = (outlineColor & 0xFF) / 255f;

            float fA = ((fillColor >> 24) & 0xFF) / 255f;
            float fR = ((fillColor >> 16) & 0xFF) / 255f;
            float fG = ((fillColor >> 8) & 0xFF) / 255f;
            float fB = (fillColor & 0xFF) / 255f;

            RenderSystem.lineWidth(lineWidth);
            // In a real implementation we would render custom solid faces + thicker lines here using VertexConsumers
            // For now, we reuse the vertexConsumer but pass our colors

            shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
                vertexConsumer.vertex(matrices.peek().getPositionMatrix(), (float)minX, (float)minY, (float)minZ).color(oR, oG, oB, oA).next();
                vertexConsumer.vertex(matrices.peek().getPositionMatrix(), (float)maxX, (float)maxY, (float)maxZ).color(oR, oG, oB, oA).next();
            });

            matrices.pop();
            RenderSystem.lineWidth(1.0f);
        }
    }
}
