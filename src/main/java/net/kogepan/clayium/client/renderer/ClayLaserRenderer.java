package net.kogepan.clayium.client.renderer;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.Config;
import net.kogepan.clayium.capability.IClayLaserSource;
import net.kogepan.clayium.laser.Laser;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

@OnlyIn(Dist.CLIENT)
public final class ClayLaserRenderer {

    private static final ResourceLocation LASER_TEXTURE = Clayium.id("textures/misc/laser.png");

    private ClayLaserRenderer() {}

    /**
     * Renders a laser beam from a block entity that implements {@link IClayLaserSource}.
     *
     * @param source        The laser source
     * @param poseStack     The pose stack (already translated to block position 0,0,0)
     * @param buffer        The buffer source
     * @param packedLight   Packed light value
     * @param packedOverlay Packed overlay value
     */
    public static void renderLaser(@NotNull IClayLaserSource source,
                                   @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                                   int packedLight, int packedOverlay) {
        Laser laser = source.getIrradiatingLaser();
        if (laser == null) return;

        renderLaser(laser, source.getDirection(), source.getLength(),
                poseStack, buffer, packedLight, packedOverlay);
    }

    /**
     * Renders a laser beam with the given parameters.
     *
     * @param laser         The laser data (RGB)
     * @param direction     Emission direction
     * @param length        Beam length in blocks
     * @param poseStack     The pose stack (already translated to block position 0,0,0)
     * @param buffer        The buffer source
     * @param packedLight   Packed light value
     * @param packedOverlay Packed overlay value
     */
    public static void renderLaser(@NotNull Laser laser, @NotNull Direction direction, int length,
                                   @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                                   int packedLight, int packedOverlay) {
        int max = Math.max(Math.max(laser.red(), laser.green()), laser.blue());
        if (max == 0) return;

        int laserQuality = getLaserQuality();

        int r = 255 * laser.red() / max;
        int g = 255 * laser.green() / max;
        int b = 255 * laser.blue() / max;

        int str = laser.red() + laser.green() + laser.blue();
        float scale = 1.0f - 14.0625f / (str + 14);
        int a = (int) ((26.0f + scale * 26.0f) * 8 / laserQuality);
        a = Math.min(255, Math.max(0, a));

        // Use full brightness so vertex colors are not darkened by lighting (Unofficial: setLightValue(15,15))
        int fullBright = LightTexture.FULL_BRIGHT;

        RenderType renderType = RenderType.entityTranslucentEmissive(LASER_TEXTURE);
        VertexConsumer consumer = buffer.getBuffer(renderType);

        poseStack.pushPose();

        // Translate to block center
        poseStack.translate(0.5, 0.5, 0.5);

        // Rotate to face emission direction
        switch (direction) {
            case UP -> {}
            case DOWN -> poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(180.0f));
            case NORTH -> poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0f));
            case SOUTH -> poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0f));
            case WEST -> poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0f));
            case EAST -> poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0f));
        }

        // Scale and extend beam (Unofficial formula)
        poseStack.scale(scale, 1.0f, scale);
        poseStack.translate(0.0, -scale / 6.0f, 0.0);
        poseStack.scale(1.0f, length + scale / 3.0f, 1.0f);

        Matrix4f matrix = poseStack.last().pose();
        Vector3f normal = new Vector3f(1, 0, 0);
        poseStack.last().normal().transform(normal);

        float rotStep = 180.0f / laserQuality;
        for (int i = 0; i < laserQuality; i++) {
            if (i > 0) {
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotStep));
                matrix = poseStack.last().pose();
                normal.set(1, 0, 0);
                poseStack.last().normal().transform(normal);
            }

            // Quad on X=0 plane: Y 0..1, Z -0.5..0.5
            // Vertices: (0,1,0.5), (0,1,-0.5), (0,0,-0.5), (0,0,0.5)
            Vector4f v0 = new Vector4f(0, 1, 0.5f, 1).mul(matrix);
            Vector4f v1 = new Vector4f(0, 1, -0.5f, 1).mul(matrix);
            Vector4f v2 = new Vector4f(0, 0, -0.5f, 1).mul(matrix);
            Vector4f v3 = new Vector4f(0, 0, 0.5f, 1).mul(matrix);

            // Use NO_OVERLAY to avoid block overlay (crack texture) darkening the laser (Original used fixed pipeline
            // with no overlay)
            int noOverlay = OverlayTexture.NO_OVERLAY;
            consumer.addVertex(v0.x, v0.y, v0.z)
                    .setColor(r, g, b, a)
                    .setUv(0, 0)
                    .setOverlay(noOverlay)
                    .setLight(fullBright)
                    .setNormal(normal.x, normal.y, normal.z);
            consumer.addVertex(v1.x, v1.y, v1.z)
                    .setColor(r, g, b, a)
                    .setUv(1, 0)
                    .setOverlay(noOverlay)
                    .setLight(fullBright)
                    .setNormal(normal.x, normal.y, normal.z);
            consumer.addVertex(v2.x, v2.y, v2.z)
                    .setColor(r, g, b, a)
                    .setUv(1, 1)
                    .setOverlay(noOverlay)
                    .setLight(fullBright)
                    .setNormal(normal.x, normal.y, normal.z);
            consumer.addVertex(v3.x, v3.y, v3.z)
                    .setColor(r, g, b, a)
                    .setUv(0, 1)
                    .setOverlay(noOverlay)
                    .setLight(fullBright)
                    .setNormal(normal.x, normal.y, normal.z);
        }

        poseStack.popPose();
    }

    private static int getLaserQuality() {
        return Config.LASER_QUALITY.get();
    }
}
