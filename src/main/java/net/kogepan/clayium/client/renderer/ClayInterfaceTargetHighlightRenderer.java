package net.kogepan.clayium.client.renderer;

import net.kogepan.clayium.Config;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.ClayInterfaceBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
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
import com.mojang.math.Axis;
import org.jetbrains.annotations.NotNull;

/**
 * Renders linked-target visuals and preview information while the player looks at a Clay Interface.
 */
public final class ClayInterfaceTargetHighlightRenderer {

    private static final float RAINBOW_COLOR_SPEED = 0.1F;
    private static final float RAINBOW_ALPHA_SPEED = 0.12F;
    private static final float ONE_THIRD_TURN = (float) (Math.PI * 2.0D / 3.0D);
    private static final double TARGET_HIGHLIGHT_INFLATE = 0.002D;
    private static final double TARGET_INFO_OFFSET = 0.8D;
    private static final float TARGET_ITEM_Y_OFFSET = 0.05F;
    private static final float TARGET_ITEM_SCALE = 0.45F;
    private static final float TARGET_ITEM_ROTATE_SPEED = 2.0F;
    private static final float TARGET_ITEM_ALPHA = 0.7F;
    private static final float TARGET_NAME_Y_OFFSET = 0.4F;
    private static final float TARGET_NAME_SCALE = 0.01F;
    private static final float TARGET_LOCATION_Y_OFFSET = 0.3F;
    private static final float TARGET_LOCATION_SCALE = 0.005F;
    private static final float TARGET_TEXT_BACKGROUND_OPACITY = 0.45F;
    private static final int TARGET_TEXT_COLOR = 0xFFFFFFFF;
    private static final int TARGET_TEXT_SEE_THROUGH_COLOR = 553648127;

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
        BlockState targetState = level.getBlockState(targetPos);

        renderTargetInfo(
                poseStack,
                bufferSource,
                camera,
                blockHitResult.getBlockPos(),
                blockHitResult.getDirection(),
                targetGlobalPos,
                targetState,
                tickTime);

        AABB targetBox = getTargetHighlightBox(level, targetPos, targetState).inflate(TARGET_HIGHLIGHT_INFLATE);
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

        bufferSource.endBatch();
    }

    @NotNull
    private static AABB getTargetHighlightBox(@NotNull Level level,
                                              @NotNull BlockPos targetPos,
                                              @NotNull BlockState targetState) {
        VoxelShape targetShape = targetState.getShape(level, targetPos);
        if (targetShape.isEmpty()) {
            return new AABB(targetPos);
        }
        return targetShape.bounds().move(targetPos);
    }

    private static void renderTargetInfo(@NotNull PoseStack poseStack,
                                         @NotNull MultiBufferSource.BufferSource bufferSource,
                                         @NotNull Vec3 camera,
                                         @NotNull BlockPos interfacePos,
                                         @NotNull Direction hitFace,
                                         @NotNull GlobalPos targetGlobalPos,
                                         @NotNull BlockState targetState,
                                         float tickTime) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) {
            return;
        }

        BlockPos targetPos = targetGlobalPos.pos();
        ItemStack targetDisplayStack = targetState.getBlock().getCloneItemStack(level, targetPos, targetState);
        if (targetDisplayStack.isEmpty()) {
            targetDisplayStack = new ItemStack(targetState.getBlock());
        }

        String targetName = targetDisplayStack.isEmpty() ? targetState.getBlock().getName().getString() :
                targetDisplayStack.getHoverName().getString();
        String targetLocation = formatTargetLocation(targetGlobalPos);

        Vec3 infoAnchor = Vec3.atCenterOf(interfacePos).add(
                hitFace.getStepX() * TARGET_INFO_OFFSET,
                hitFace.getStepY() * TARGET_INFO_OFFSET,
                hitFace.getStepZ() * TARGET_INFO_OFFSET);
        Vec3 localAnchor = infoAnchor.subtract(camera);

        poseStack.pushPose();
        poseStack.translate(localAnchor.x, localAnchor.y, localAnchor.z);

        if (!targetDisplayStack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.0F, TARGET_ITEM_Y_OFFSET, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(tickTime * TARGET_ITEM_ROTATE_SPEED));
            poseStack.scale(TARGET_ITEM_SCALE, TARGET_ITEM_SCALE, TARGET_ITEM_SCALE);
            MultiBufferSource translucentItemBuffer = createTranslucentItemBuffer(bufferSource, TARGET_ITEM_ALPHA);
            minecraft.getItemRenderer().renderStatic(
                    targetDisplayStack,
                    ItemDisplayContext.FIXED,
                    LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    translucentItemBuffer,
                    level,
                    targetPos.hashCode());
            poseStack.popPose();
        }

        renderBillboardText(poseStack, bufferSource, TARGET_NAME_Y_OFFSET, TARGET_NAME_SCALE, targetName);
        renderBillboardText(
                poseStack,
                bufferSource,
                TARGET_LOCATION_Y_OFFSET,
                TARGET_LOCATION_SCALE,
                targetLocation);

        poseStack.popPose();
    }

    @NotNull
    private static MultiBufferSource createTranslucentItemBuffer(@NotNull MultiBufferSource.BufferSource delegate,
                                                                 float alphaMultiplier) {
        return renderType -> new AlphaMultiplyingVertexConsumer(
                delegate.getBuffer(remapItemRenderType(renderType)),
                alphaMultiplier);
    }

    @NotNull
    private static RenderType remapItemRenderType(@NotNull RenderType renderType) {
        if (renderType == Sheets.solidBlockSheet() ||
                renderType == Sheets.cutoutBlockSheet() ||
                renderType == Sheets.translucentCullBlockSheet()) {
            return Sheets.translucentItemSheet();
        }
        return renderType;
    }

    @NotNull
    private static String formatTargetLocation(@NotNull GlobalPos targetGlobalPos) {
        BlockPos targetPos = targetGlobalPos.pos();
        String dimensionName = Component.translatable(targetGlobalPos.dimension().location().toLanguageKey("dimension"))
                .getString();
        return targetPos.getX() + ", " + targetPos.getY() + ", " + targetPos.getZ() + "; " + dimensionName;
    }

    private static void renderBillboardText(@NotNull PoseStack poseStack,
                                            @NotNull MultiBufferSource.BufferSource bufferSource,
                                            float yOffset,
                                            float scale,
                                            @NotNull String text) {
        if (text.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int backgroundColor = (int) (minecraft.options.getBackgroundOpacity(TARGET_TEXT_BACKGROUND_OPACITY) * 255.0F) <<
                24;

        poseStack.pushPose();
        poseStack.translate(0.0F, yOffset, 0.0F);
        poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(scale, -scale, scale);

        float x = -font.width(text) / 2.0F;
        PoseStack.Pose pose = poseStack.last();
        font.drawInBatch(
                text,
                x,
                0.0F,
                TARGET_TEXT_SEE_THROUGH_COLOR,
                false,
                pose.pose(),
                bufferSource,
                Font.DisplayMode.SEE_THROUGH,
                backgroundColor,
                LightTexture.FULL_BRIGHT);
        font.drawInBatch(
                text,
                x,
                0.0F,
                TARGET_TEXT_COLOR,
                false,
                pose.pose(),
                bufferSource,
                Font.DisplayMode.NORMAL,
                0,
                LightTexture.FULL_BRIGHT);
        poseStack.popPose();
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

    private static final class AlphaMultiplyingVertexConsumer implements VertexConsumer {

        private final VertexConsumer delegate;
        private final float alphaMultiplier;

        private AlphaMultiplyingVertexConsumer(@NotNull VertexConsumer delegate, float alphaMultiplier) {
            this.delegate = delegate;
            this.alphaMultiplier = Mth.clamp(alphaMultiplier, 0.0F, 1.0F);
        }

        @Override
        public @NotNull VertexConsumer addVertex(float x, float y, float z) {
            this.delegate.addVertex(x, y, z);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setColor(int red, int green, int blue, int alpha) {
            int scaledAlpha = Mth.clamp(Math.round(alpha * this.alphaMultiplier), 0, 255);
            this.delegate.setColor(red, green, blue, scaledAlpha);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv(float u, float v) {
            this.delegate.setUv(u, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv1(int u, int v) {
            this.delegate.setUv1(u, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv2(int u, int v) {
            this.delegate.setUv2(u, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
            this.delegate.setNormal(normalX, normalY, normalZ);
            return this;
        }
    }

    private static float rainbowComponent(float tickTime, float phase) {
        return (float) ((Math.sin(tickTime * RAINBOW_COLOR_SPEED + phase) + 1.0D) * 0.5D);
    }
}
