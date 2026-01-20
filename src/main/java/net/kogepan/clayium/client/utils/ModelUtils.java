package net.kogepan.clayium.client.utils;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.SimpleModelState;

import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.List;
import java.util.function.Function;

public class ModelUtils {

    @Nullable
    public static BakedQuad getQuad(@NotNull BakedModel model, @NotNull Direction direction) {
        List<BakedQuad> quads = model.getQuads(null, direction, RandomSource.create());
        return quads.isEmpty() ? null : quads.getFirst();
    }

    public static BakedModel rotateModel(@NotNull BlockModel model, @NotNull ModelBaker baker,
                                         @NotNull Function<Material, TextureAtlasSprite> spriteGetter,
                                         @NotNull Transformation root, float degree) {
        Quaternionf rotation = new Quaternionf(root.getLeftRotation());
        rotation.mul(Axis.YP.rotationDegrees(degree));
        ModelState rotatedState = new SimpleModelState(new Transformation(
                root.getTranslation(),
                rotation,
                root.getScale(),
                root.getRightRotation()));

        return model.bake(baker, spriteGetter, rotatedState);
    }

    public static BakedQuad remapQuadUVToRange(
                                               @NotNull BakedQuad sourceQuad,
                                               @NotNull BakedQuad targetQuad,
                                               float uStart,
                                               float uEnd,
                                               float vStart,
                                               float vEnd) {
        // Get sprites
        TextureAtlasSprite targetSprite = targetQuad.getSprite();
        TextureAtlasSprite sourceSprite = sourceQuad.getSprite();

        // Pre-calculate conversion factors (uStart/16.0f, etc. are constant)
        float uStartFactor = uStart / 16.0f;
        float uEndFactor = uEnd / 16.0f;
        float vStartFactor = vStart / 16.0f;
        float vEndFactor = vEnd / 16.0f;

        // Calculate target sprite UV range
        int targetSpriteWidth = targetSprite.contents().width();
        int targetSpriteHeight = targetSprite.contents().height();
        float targetUMin = targetSprite.getU(targetSpriteWidth * uStartFactor);
        float targetUMax = targetSprite.getU(targetSpriteWidth * uEndFactor);
        float targetVMin = targetSprite.getV(targetSpriteHeight * vStartFactor);
        float targetVMax = targetSprite.getV(targetSpriteHeight * vEndFactor);

        // Calculate source sprite UV range
        int sourceSpriteWidth = sourceSprite.contents().width();
        int sourceSpriteHeight = sourceSprite.contents().height();
        float sourceUMin = sourceSprite.getU(sourceSpriteWidth * uStartFactor);
        float sourceUMax = sourceSprite.getU(sourceSpriteWidth * uEndFactor);
        float sourceVMin = sourceSprite.getV(sourceSpriteHeight * vStartFactor);
        float sourceVMax = sourceSprite.getV(sourceSpriteHeight * vEndFactor);

        // Pre-calculate denominators for normalization (avoid division in loop)
        float sourceURange = sourceUMax - sourceUMin;
        float sourceVRange = sourceVMax - sourceVMin;
        float targetURange = targetUMax - targetUMin;
        float targetVRange = targetVMax - targetVMin;

        // Copy the vertex data of sourceQuad
        int[] vertices = sourceQuad.getVertices().clone();

        // Update UV coordinates of each vertex
        for (int i = 0; i < 4; i++) {
            int vertexIndex = i * 8;

            // Get the current UV coordinates
            float currentU = Float.intBitsToFloat(vertices[vertexIndex + 4]);
            float currentV = Float.intBitsToFloat(vertices[vertexIndex + 5]);

            // Calculate the relative position within sourceQuad's specified range (0.0-1.0)
            float normalizedU = (currentU - sourceUMin) / sourceURange;
            float normalizedV = (currentV - sourceVMin) / sourceVRange;

            // Map to the UV range of targetQuad's specified range
            float finalU = targetUMin + normalizedU * targetURange;
            float finalV = targetVMin + normalizedV * targetVRange;

            // Update vertex data
            vertices[vertexIndex + 4] = Float.floatToRawIntBits(finalU);
            vertices[vertexIndex + 5] = Float.floatToRawIntBits(finalV);
        }

        return new BakedQuad(
                vertices,
                sourceQuad.getTintIndex(),
                sourceQuad.getDirection(),
                targetSprite,
                sourceQuad.isShade());
    }
}
