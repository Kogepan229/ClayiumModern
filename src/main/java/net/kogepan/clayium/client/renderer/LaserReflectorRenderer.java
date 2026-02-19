package net.kogepan.clayium.client.renderer;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blockentities.machine.LaserReflectorBlockEntity;
import net.kogepan.clayium.blocks.machine.LaserReflectorBlock;
import net.kogepan.clayium.capability.IClayLaserSource;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

/**
 * Renders the Laser Reflector block with the same pyramid shape as ClayiumOriginal.
 * <p>
 * Shape: flat base + 4 triangular faces forming a pyramid. The apex points in the facing direction.
 */
@net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
public class LaserReflectorRenderer implements BlockEntityRenderer<LaserReflectorBlockEntity> {

    private static final ResourceLocation TEXTURE = Clayium.id("block/machine/laser_reflector");
    private static final float F = 0.125f;

    public LaserReflectorRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(@NotNull LaserReflectorBlockEntity blockEntity, float partialTick,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        // Render laser beam
        if (blockEntity.getIrradiatingLaser() != null) {
            ClayLaserRenderer.renderLaser(blockEntity, poseStack, buffer, packedLight, packedOverlay);
        }

        // Render pyramid block shape (matching ClayiumOriginal)
        TextureAtlasSprite sprite = net.minecraft.client.Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(TEXTURE);

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        Direction facing = blockEntity.getBlockState().getValue(LaserReflectorBlock.FACING);

        poseStack.pushPose();

        // Rotate around block center (0.5, 0.5, 0.5) so the pyramid faces the correct direction
        poseStack.translate(0.5, 0.5, 0.5);
        switch (facing) {
            case UP -> {}
            case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(180.0f));
            case NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(-90.0f));
            case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));
            case WEST -> poseStack.mulPose(Axis.ZP.rotationDegrees(90.0f));
            case EAST -> poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0f));
        }
        poseStack.translate(-0.5, -0.5, -0.5);

        // Use block atlas for block texture; UV comes from sprite
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS));
        var pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        // Base quad (at y=F, facing -Y)
        float x0 = F * 2;
        float x1 = 1 - F * 2;
        float z0 = F * 2;
        float z1 = 1 - F * 2;
        float yBase = F;

        addQuad(consumer, pose, matrix, packedLight, packedOverlay,
                new Vec3(x0, yBase, z0), new Vec3(x1, yBase, z0), new Vec3(x1, yBase, z1), new Vec3(x0, yBase, z1),
                u0, v0, u1, v1, 0, -1, 0);

        // 4 triangular faces: apex at (0.5, 1-F, 0.5), each face uses its own base edge
        float apexX = 0.5f;
        float apexY = 1 - F;
        float apexZ = 0.5f;

        // Face 1: edge at z=z0 (front)
        addTriangle(consumer, pose, matrix, packedLight, packedOverlay,
                apexX, apexY, apexZ, x1, yBase, z0, x0, yBase, z0, u0, v0, u1, v1);
        // Face 2: edge at x=x1 (right)
        addTriangle(consumer, pose, matrix, packedLight, packedOverlay,
                apexX, apexY, apexZ, x1, yBase, z1, x1, yBase, z0, u0, v0, u1, v1);
        // Face 3: edge at z=z1 (back)
        addTriangle(consumer, pose, matrix, packedLight, packedOverlay,
                apexX, apexY, apexZ, x0, yBase, z1, x1, yBase, z1, u0, v0, u1, v1);
        // Face 4: edge at x=x0 (left)
        addTriangle(consumer, pose, matrix, packedLight, packedOverlay,
                apexX, apexY, apexZ, x0, yBase, z0, x0, yBase, z1, u0, v0, u1, v1);

        poseStack.popPose();
    }

    private static void addQuad(VertexConsumer consumer, PoseStack.Pose pose, Matrix4f matrix,
                                int packedLight, int packedOverlay,
                                Vec3 va, Vec3 vb, Vec3 vc, Vec3 vd,
                                float uMin, float vMin, float uMax, float vMax,
                                float nx, float ny, float nz) {
        consumer.addVertex(matrix, (float) va.x(), (float) va.y(), (float) va.z())
                .setColor(255, 255, 255, 225)
                .setUv(uMin, vMin)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(pose, nx, ny, nz);
        consumer.addVertex(matrix, (float) vb.x(), (float) vb.y(), (float) vb.z())
                .setColor(255, 255, 255, 225)
                .setUv(uMax, vMin)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(pose, nx, ny, nz);
        consumer.addVertex(matrix, (float) vc.x(), (float) vc.y(), (float) vc.z())
                .setColor(255, 255, 255, 225)
                .setUv(uMax, vMax)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(pose, nx, ny, nz);
        consumer.addVertex(matrix, (float) vd.x(), (float) vd.y(), (float) vd.z())
                .setColor(255, 255, 255, 225)
                .setUv(uMin, vMax)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(pose, nx, ny, nz);
    }

    private static void addTriangle(VertexConsumer consumer, PoseStack.Pose pose, Matrix4f matrix,
                                    int packedLight, int packedOverlay,
                                    float x1, float y1, float z1,
                                    float x2, float y2, float z2,
                                    float x3, float y3, float z3,
                                    float uMin, float vMin, float uMax, float vMax) {
        // Compute face normal
        float ax = x2 - x1, ay = y2 - y1, az = z2 - z1;
        float bx = x3 - x1, by = y3 - y1, bz = z3 - z1;
        float nx = ay * bz - az * by;
        float ny = az * bx - ax * bz;
        float nz = ax * by - ay * bx;
        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (len > 0) {
            nx /= len;
            ny /= len;
            nz /= len;
        }

        consumer.addVertex(matrix, x1, y1, z1)
                .setColor(255, 255, 255, 225)
                .setUv(uMax, vMin)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(pose, nx, ny, nz);
        consumer.addVertex(matrix, x2, y2, z2)
                .setColor(255, 255, 255, 225)
                .setUv(uMax, vMax)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(pose, nx, ny, nz);
        consumer.addVertex(matrix, x3, y3, z3)
                .setColor(255, 255, 255, 225)
                .setUv(uMin, vMax)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(pose, nx, ny, nz);
        consumer.addVertex(matrix, x1, y1, z1)
                .setColor(255, 255, 255, 225)
                .setUv(uMax, vMin)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(pose, nx, ny, nz);
    }

    @Override
    @NotNull
    public AABB getRenderBoundingBox(@NotNull LaserReflectorBlockEntity blockEntity) {
        if (blockEntity instanceof IClayLaserSource source && source.getIrradiatingLaser() != null) {
            int length = source.getLength();
            if (length > 0) {
                Direction dir = source.getDirection();
                var step = dir.getNormal();
                double minX = blockEntity.getBlockPos().getX() + Math.min(0, step.getX() * length);
                double maxX = blockEntity.getBlockPos().getX() + 1 + Math.max(0, step.getX() * length);
                double minY = blockEntity.getBlockPos().getY() + Math.min(0, step.getY() * length);
                double maxY = blockEntity.getBlockPos().getY() + 1 + Math.max(0, step.getY() * length);
                double minZ = blockEntity.getBlockPos().getZ() + Math.min(0, step.getZ() * length);
                double maxZ = blockEntity.getBlockPos().getZ() + 1 + Math.max(0, step.getZ() * length);
                return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
            }
        }
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
    }
}
