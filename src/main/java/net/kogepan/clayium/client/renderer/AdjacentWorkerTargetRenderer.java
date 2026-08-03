package net.kogepan.clayium.client.renderer;

import net.kogepan.clayium.blockentities.machine.AdjacentWorkerBlockEntity;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public final class AdjacentWorkerTargetRenderer {

    private static final double BOX_INFLATE = 0.002D;
    private static final float FILL_ALPHA = 0.18F;

    private AdjacentWorkerTargetRenderer() {}

    public static void render(@NotNull RenderLevelStageEvent event,
                              @NotNull MultiBufferSource.BufferSource bufferSource) {
        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        boolean[] rendered = new boolean[1];

        event.getLevelRenderer().iterateVisibleBlockEntities(blockEntity -> {
            if (!(blockEntity instanceof AdjacentWorkerBlockEntity worker) ||
                    worker.getTargetDisplayMode() == AdjacentWorkerBlockEntity.TargetDisplayMode.OFF) {
                return;
            }
            boolean xray = worker.getTargetDisplayMode() == AdjacentWorkerBlockEntity.TargetDisplayMode.XRAY;
            RenderType fillType = xray ? ClayiumRenderTypes.INTERFACE_TARGET_FILLED_NO_DEPTH :
                    RenderType.debugFilledBox();
            RenderType lineType = xray ? ClayiumRenderTypes.INTERFACE_TARGET_LINES_NO_DEPTH : RenderType.lines();
            AABB workBox = new AABB(worker.getWorkPos()).inflate(BOX_INFLATE);
            AABB machineBox = new AABB(worker.getBlockPos()).inflate(BOX_INFLATE);

            VertexConsumer fillConsumer = bufferSource.getBuffer(fillType);
            renderFilledBox(poseStack, fillConsumer, workBox, camera, 0.0F, 0.35F, 1.0F);
            renderFilledBox(poseStack, fillConsumer, machineBox, camera, 1.0F, 0.0F, 0.0F);

            VertexConsumer lineConsumer = bufferSource.getBuffer(lineType);
            renderLineBox(poseStack, lineConsumer, workBox, camera, 0.0F, 0.35F, 1.0F);
            renderLineBox(poseStack, lineConsumer, machineBox, camera, 1.0F, 0.0F, 0.0F);
            rendered[0] = true;
        });

        if (rendered[0]) {
            bufferSource.endBatch();
        }
    }

    private static void renderFilledBox(@NotNull PoseStack poseStack,
                                        @NotNull VertexConsumer consumer,
                                        @NotNull AABB box,
                                        @NotNull Vec3 camera,
                                        float red, float green, float blue) {
        double minX = box.minX - camera.x;
        double minY = box.minY - camera.y;
        double minZ = box.minZ - camera.z;
        double maxX = box.maxX - camera.x;
        double maxY = box.maxY - camera.y;
        double maxZ = box.maxZ - camera.z;
        LevelRenderer.addChainedFilledBoxVertices(poseStack, consumer,
                minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, FILL_ALPHA);
    }

    private static void renderLineBox(@NotNull PoseStack poseStack,
                                      @NotNull VertexConsumer consumer,
                                      @NotNull AABB box,
                                      @NotNull Vec3 camera,
                                      float red, float green, float blue) {
        double minX = box.minX - camera.x;
        double minY = box.minY - camera.y;
        double minZ = box.minZ - camera.z;
        double maxX = box.maxX - camera.x;
        double maxY = box.maxY - camera.y;
        double maxZ = box.maxZ - camera.z;
        LevelRenderer.renderLineBox(poseStack, consumer,
                minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, 1.0F);
    }
}
