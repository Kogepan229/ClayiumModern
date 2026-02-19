package net.kogepan.clayium.client.renderer;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.capability.IClayLaserSource;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;

import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.NotNull;

/**
 * Main renderer for ClayContainerBlockEntity.
 * Delegates to {@link ClayLaserRenderer} for laser beams and {@link PipedMachineIoRenderer}
 * for pipe IO icons.
 */
public class ClayContainerRenderer implements BlockEntityRenderer<ClayContainerBlockEntity> {

    private final PipedMachineIoRenderer pipeRenderer;

    public ClayContainerRenderer(BlockEntityRendererProvider.Context context) {
        this.pipeRenderer = new PipedMachineIoRenderer(context);
    }

    @Override
    public void render(@NotNull ClayContainerBlockEntity blockEntity, float partialTick,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        // Render laser beam (for Clay Laser and other IClayLaserSource blocks)
        if (blockEntity instanceof IClayLaserSource source && source.getIrradiatingLaser() != null) {
            ClayLaserRenderer.renderLaser(source, poseStack, buffer, packedLight, packedOverlay);
        }

        // Render pipe IO icons
        pipeRenderer.render(blockEntity, partialTick, poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    @NotNull
    public AABB getRenderBoundingBox(@NotNull ClayContainerBlockEntity blockEntity) {
        if (blockEntity instanceof IClayLaserSource source && source.getIrradiatingLaser() != null) {
            int length = source.getLength();
            if (length > 0) {
                Direction dir = source.getDirection();
                Vec3i step = dir.getNormal();
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
