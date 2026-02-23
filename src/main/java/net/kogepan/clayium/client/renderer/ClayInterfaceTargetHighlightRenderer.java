package net.kogepan.clayium.client.renderer;

import net.kogepan.clayium.Config;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.ClayInterfaceBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * Renders rainbow target highlight visuals when the player looks at a Clay Interface.
 */
public final class ClayInterfaceTargetHighlightRenderer {

    private static final float RAINBOW_COLOR_SPEED = 0.1F;
    private static final float RAINBOW_ALPHA_SPEED = 0.12F;
    private static final float ONE_THIRD_TURN = (float) (Math.PI * 2.0D / 3.0D);
    private static final double TARGET_HIGHLIGHT_INFLATE = 0.002D;

    private ClayInterfaceTargetHighlightRenderer() {}

    public static void render(@NotNull RenderLevelStageEvent event,
                              @NotNull MultiBufferSource.BufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) {
            return;
        }

        if (!(minecraft.hitResult instanceof BlockHitResult blockHitResult) ||
                blockHitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockEntity lookedAtBlockEntity = level.getBlockEntity(blockHitResult.getBlockPos());
        if (!(lookedAtBlockEntity instanceof ClayInterfaceBlockEntity interfaceBlockEntity)) {
            return;
        }
        if (!interfaceBlockEntity.hasValidTarget()) {
            return;
        }

        GlobalPos targetGlobalPos = interfaceBlockEntity.getLinkedTargetPos();
        if (targetGlobalPos == null || !targetGlobalPos.dimension().equals(level.dimension())) {
            return;
        }

        BlockPos targetPos = targetGlobalPos.pos();
        if (!level.isLoaded(targetPos)) {
            return;
        }

        BlockEntity targetBlockEntity = level.getBlockEntity(targetPos);
        if (!(targetBlockEntity instanceof ClayContainerBlockEntity) ||
                targetBlockEntity instanceof ClayInterfaceBlockEntity) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        Vec3 camera = event.getCamera().getPosition();
        float tickTime = (float) level.getGameTime() + event.getPartialTick().getGameTimeDeltaPartialTick(false);
        float red = rainbowComponent(tickTime, 0.0F);
        float green = rainbowComponent(tickTime, ONE_THIRD_TURN);
        float blue = rainbowComponent(tickTime, ONE_THIRD_TURN * 2.0F);
        float alpha = (float) ((Math.sin(tickTime * RAINBOW_ALPHA_SPEED) + 1.0D) * 0.15D + 0.2D);

        AABB targetBox = getTargetHighlightBox(level, targetPos).inflate(TARGET_HIGHLIGHT_INFLATE);
        double minX = targetBox.minX - camera.x;
        double minY = targetBox.minY - camera.y;
        double minZ = targetBox.minZ - camera.z;
        double maxX = targetBox.maxX - camera.x;
        double maxY = targetBox.maxY - camera.y;
        double maxZ = targetBox.maxZ - camera.z;

        VertexConsumer fillConsumer = bufferSource.getBuffer(ClayiumRenderTypes.INTERFACE_TARGET_FILLED_NO_DEPTH);
        LevelRenderer.addChainedFilledBoxVertices(
                poseStack, fillConsumer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);

        VertexConsumer lineConsumer = bufferSource.getBuffer(ClayiumRenderTypes.INTERFACE_TARGET_LINES_NO_DEPTH);
        if (Config.INTERFACE_TARGET_HIGHLIGHT_OUTLINE.get()) {
            LevelRenderer.renderLineBox(
                    poseStack, lineConsumer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue,
                    1.0F);
        }

        renderConnectorLine(
                poseStack,
                lineConsumer,
                camera,
                blockHitResult.getBlockPos(),
                targetPos,
                red,
                green,
                blue,
                alpha);

        bufferSource.endBatch(ClayiumRenderTypes.INTERFACE_TARGET_FILLED_NO_DEPTH);
        bufferSource.endBatch(ClayiumRenderTypes.INTERFACE_TARGET_LINES_NO_DEPTH);
    }

    @NotNull
    private static AABB getTargetHighlightBox(@NotNull Level level, @NotNull BlockPos targetPos) {
        BlockState targetState = level.getBlockState(targetPos);
        VoxelShape targetShape = targetState.getShape(level, targetPos);
        if (targetShape.isEmpty()) {
            return new AABB(targetPos);
        }
        return targetShape.bounds().move(targetPos);
    }

    private static void renderConnectorLine(@NotNull PoseStack poseStack,
                                            @NotNull VertexConsumer lineConsumer,
                                            @NotNull Vec3 camera,
                                            @NotNull BlockPos sourcePos,
                                            @NotNull BlockPos targetPos,
                                            float red,
                                            float green,
                                            float blue,
                                            float alpha) {
        Vec3 sourceCenter = Vec3.atCenterOf(sourcePos).subtract(camera);
        Vec3 targetCenter = Vec3.atCenterOf(targetPos).subtract(camera);

        float dx = (float) (targetCenter.x - sourceCenter.x);
        float dy = (float) (targetCenter.y - sourceCenter.y);
        float dz = (float) (targetCenter.z - sourceCenter.z);
        float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        float nx = 0.0F;
        float ny = 1.0F;
        float nz = 0.0F;
        if (length > 0.0F) {
            nx = dx / length;
            ny = dy / length;
            nz = dz / length;
        }

        PoseStack.Pose pose = poseStack.last();
        lineConsumer.addVertex(pose, (float) sourceCenter.x, (float) sourceCenter.y, (float) sourceCenter.z)
                .setColor(red, green, blue, alpha)
                .setNormal(pose, nx, ny, nz);
        lineConsumer.addVertex(pose, (float) targetCenter.x, (float) targetCenter.y, (float) targetCenter.z)
                .setColor(red, green, blue, alpha)
                .setNormal(pose, nx, ny, nz);
    }

    private static float rainbowComponent(float tickTime, float phase) {
        return (float) ((Math.sin(tickTime * RAINBOW_COLOR_SPEED + phase) + 1.0D) * 0.5D);
    }
}
