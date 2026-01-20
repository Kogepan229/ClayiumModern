package net.kogepan.clayium.client.model.block;

import net.kogepan.clayium.client.utils.ModelUtils;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class BakedPipeModel implements BakedModel {

    private final List<BakedQuad> unculledFaces = new ArrayList<>();
    private final Map<Direction, List<BakedQuad>> culledFaces = new EnumMap<>(Direction.class);
    private final TextureAtlasSprite particleIcon;

    public BakedPipeModel(@NotNull BakedModel base, BakedModel basePipe) {
        for (Direction direction : Direction.values()) {
            BakedQuad quad = ModelUtils.getQuad(basePipe, direction);
            BakedQuad quadBase = ModelUtils.getQuad(base, direction);
            if (quad == null || quadBase == null) {
                culledFaces.put(direction, new ArrayList<>());
                continue;
            }

            BakedQuad newQuad = ModelUtils.remapQuadUVToRange(quad, quadBase, 5, 11, 5, 11);
            culledFaces.put(direction, List.of(newQuad));
        }

        for (BakedQuad quad : basePipe.getQuads(null, null, RandomSource.create())) {
            Direction direction = quad.getDirection();
            BakedQuad quadBase = ModelUtils.getQuad(base, direction);
            if (quadBase == null) {
                unculledFaces.add(quad);
                continue;
            }

            // Calculate face positions of each vertex when the face is full-sized (16*16)
            int[] vertices = quad.getVertices();
            float[] facePositions = new float[4];
            for (int i = 0; i < 4; i++) {
                int vertexIndex = i * 8;
                float x = Float.intBitsToFloat(vertices[vertexIndex]);
                float z = Float.intBitsToFloat(vertices[vertexIndex + 2]);

                // Calculate face coordinates based on direction (range: 0-16)
                float facePos;
                switch (direction) {
                    case NORTH, SOUTH, UP, DOWN -> facePos = x * 16.0f; // Use x coordinate
                    case EAST, WEST -> facePos = z * 16.0f; // Use z coordinate
                    default -> facePos = 0.0f;
                }
                facePositions[i] = facePos;
            }

            BakedQuad newQuad = ModelUtils.remapQuadUVToRange(quad, quadBase, facePositions[0], facePositions[2],
                    facePositions[1], facePositions[3]);
            unculledFaces.add(newQuad);
        }

        this.particleIcon = base.getParticleIcon();
    }

    @Override
    @NotNull
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction,
                                    @NotNull RandomSource randomSource) {
        return direction == null ? this.unculledFaces : this.culledFaces.get(direction);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    @NotNull
    public TextureAtlasSprite getParticleIcon() {
        return this.particleIcon;
    }

    @Override
    @NotNull
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
