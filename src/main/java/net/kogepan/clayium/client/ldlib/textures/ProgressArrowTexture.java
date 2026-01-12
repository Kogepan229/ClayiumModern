package net.kogepan.clayium.client.ldlib.textures;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import com.lowdragmc.lowdraglib2.LDLib2;
import com.lowdragmc.lowdraglib2.client.shader.LDLibRenderTypes;
import com.lowdragmc.lowdraglib2.gui.texture.TransformTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;

public class ProgressArrowTexture extends TransformTexture {

    private static final ResourceLocation SPRITE = LDLib2.id("textures/gui/progress_bar_arrow.png");

    @Getter
    private float progress = 0.0f;

    public void setProgress(float progress) {
        this.progress = Math.max(0.0f, Math.min(1.0f, progress));
    }

    private static final float FULL_WIDTH = 20.0f;
    private static final float LEFT_WIDTH = 1.0f;
    private static final float RIGHT_WIDTH = 8.0f;
    private static final float MID_WIDTH = FULL_WIDTH - LEFT_WIDTH - RIGHT_WIDTH;

    private static void drawArrow(GuiGraphics graphics, float x, float y, float width, float height, float ratio,
                                  boolean isEmpty) {
        var matrix = graphics.pose().last().pose();
        var buffer = graphics.bufferSource().getBuffer(LDLibRenderTypes.guiTexture(SPRITE));
        RenderSystem.disableDepthTest();
        final int color = 0xFFFFFFFF;

        // Texture UV（0.0-1.0）
        final float texLeft = LEFT_WIDTH / FULL_WIDTH;        // 1/20 = 0.05
        final float texMidStart = LEFT_WIDTH / FULL_WIDTH;    // 1/20 = 0.05
        final float texMidEnd = (LEFT_WIDTH + MID_WIDTH) / FULL_WIDTH; // 12/20 = 0.6
        final float texRightStart = (LEFT_WIDTH + MID_WIDTH) / FULL_WIDTH; // 12/20 = 0.6
        final float texRightEnd = 1.0f;                   // 20/20 = 1.0

        final float v0 = isEmpty ? 0.0f : 0.5f;
        final float v1 = isEmpty ? 0.5f : 1.0f;

        final float midWidth = width - LEFT_WIDTH - RIGHT_WIDTH;
        final float totalDrawWidth = width * ratio;

        // left
        if (totalDrawWidth > 0) {
            final float leftEndX = Math.min(x + LEFT_WIDTH, x + totalDrawWidth);
            final float leftProgress = Math.min(1.0f, totalDrawWidth / LEFT_WIDTH);
            final float leftU2 = texLeft * leftProgress;

            buffer.addVertex(matrix, x, y + height, 0).setUv(0.0f, v1).setColor(color);
            buffer.addVertex(matrix, leftEndX, y + height, 0).setUv(leftU2, v1).setColor(color);
            buffer.addVertex(matrix, leftEndX, y, 0).setUv(leftU2, v0).setColor(color);
            buffer.addVertex(matrix, x, y, 0).setUv(0.0f, v0).setColor(color);
        }

        // middle
        if (totalDrawWidth > LEFT_WIDTH) {
            final float midStartX = x + LEFT_WIDTH;
            final float midEndX = Math.min(x + LEFT_WIDTH + midWidth, x + totalDrawWidth);
            final float midProgress = Math.min(1.0f, (totalDrawWidth - LEFT_WIDTH) / midWidth);
            final float midU1 = texMidStart;
            final float midU2 = texMidStart + (texMidEnd - texMidStart) * midProgress;

            buffer.addVertex(matrix, midStartX, y + height, 0).setUv(midU1, v1).setColor(color);
            buffer.addVertex(matrix, midEndX, y + height, 0).setUv(midU2, v1).setColor(color);
            buffer.addVertex(matrix, midEndX, y, 0).setUv(midU2, v0).setColor(color);
            buffer.addVertex(matrix, midStartX, y, 0).setUv(midU1, v0).setColor(color);
        }

        // right
        if (totalDrawWidth > LEFT_WIDTH + midWidth) {
            final float rightStartX = x + LEFT_WIDTH + midWidth;
            final float rightEndX = Math.min(x + width, x + totalDrawWidth);
            final float rightProgress = Math.min(1.0f, (totalDrawWidth - LEFT_WIDTH - midWidth) / RIGHT_WIDTH);
            final float rightU1 = texRightStart;
            final float rightU2 = texRightStart + (texRightEnd - texRightStart) * rightProgress;

            buffer.addVertex(matrix, rightStartX, y + height, 0).setUv(rightU1, v1).setColor(color);
            buffer.addVertex(matrix, rightEndX, y + height, 0).setUv(rightU2, v1).setColor(color);
            buffer.addVertex(matrix, rightEndX, y, 0).setUv(rightU2, v0).setColor(color);
            buffer.addVertex(matrix, rightStartX, y, 0).setUv(rightU1, v0).setColor(color);
        }
    }

    @Override
    protected void drawInternal(GuiGraphics graphics, float mouseX, float mouseY, float x, float y, float width,
                                float height, float partialTicks) {
        drawArrow(graphics, x, y, width, height, 1.0f, true);
        if (this.progress > 0.0f) {
            drawArrow(graphics, x, y, width, height, this.progress, false);
        }
    }
}
