package net.kogepan.clayium.client.model.block;

import net.kogepan.clayium.Clayium;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class UnbakedClayContainerModel implements IUnbakedGeometry<UnbakedClayContainerModel> {

    ResourceLocation PIPE_CORE_MODEL = Clayium.id("block/pipe/core");
    ResourceLocation PIPE_NORTH_MODEL = Clayium.id("block/pipe/north");
    ResourceLocation PIPE_WEST_MODEL = Clayium.id("block/pipe/west");
    ResourceLocation PIPE_SOUTH_MODEL = Clayium.id("block/pipe/south");
    ResourceLocation PIPE_EAST_MODEL = Clayium.id("block/pipe/east");
    ResourceLocation PIPE_UP_MODEL = Clayium.id("block/pipe/up");
    ResourceLocation PIPE_DOWN_MODEL = Clayium.id("block/pipe/down");

    private final BlockModel base;
    @Nullable
    private final BlockModel overlay;

    public UnbakedClayContainerModel(BlockModel base, @Nullable BlockModel overlay) {
        this.base = base;
        this.overlay = overlay;
    }

    @Override
    @NotNull
    public BakedModel bake(@NotNull IGeometryBakingContext context, @NotNull ModelBaker baker,
                           @NotNull Function<Material, TextureAtlasSprite> spriteGetter, @NotNull ModelState modelState,
                           @NotNull ItemOverrides overrides) {
        BakedModel bakedBase = this.base.bake(baker, spriteGetter, modelState);

        Map<Direction, BakedModel> bakedOverlays = null;
        if (this.overlay != null) {

            bakedOverlays = new EnumMap<>(Direction.class);
            bakedOverlays.put(Direction.NORTH,
                    rotateModel(this.overlay, baker, spriteGetter, context.getRootTransform(), 0));
            bakedOverlays.put(Direction.WEST,
                    rotateModel(this.overlay, baker, spriteGetter, context.getRootTransform(), 90));
            bakedOverlays.put(Direction.SOUTH,
                    rotateModel(this.overlay, baker, spriteGetter, context.getRootTransform(), 180));
            bakedOverlays.put(Direction.EAST,
                    rotateModel(this.overlay, baker, spriteGetter, context.getRootTransform(), 270));
        }

        BakedModel pipeCore = new BakedPipeModel(bakedBase, baker.bake(PIPE_CORE_MODEL, modelState, spriteGetter));
        Map<Direction, BakedModel> pipeArms = new EnumMap<>(Direction.class);
        pipeArms.put(Direction.NORTH,
                new BakedPipeModel(bakedBase, baker.bake(PIPE_NORTH_MODEL, modelState, spriteGetter)));
        pipeArms.put(Direction.WEST,
                new BakedPipeModel(bakedBase, baker.bake(PIPE_WEST_MODEL, modelState, spriteGetter)));
        pipeArms.put(Direction.SOUTH,
                new BakedPipeModel(bakedBase, baker.bake(PIPE_SOUTH_MODEL, modelState, spriteGetter)));
        pipeArms.put(Direction.EAST,
                new BakedPipeModel(bakedBase, baker.bake(PIPE_EAST_MODEL, modelState, spriteGetter)));
        pipeArms.put(Direction.UP, new BakedPipeModel(bakedBase, baker.bake(PIPE_UP_MODEL, modelState, spriteGetter)));
        pipeArms.put(Direction.DOWN,
                new BakedPipeModel(bakedBase, baker.bake(PIPE_DOWN_MODEL, modelState, spriteGetter)));

        return new ClayContainerModel(bakedBase, bakedOverlays, pipeCore, pipeArms);
    }

    private static BakedQuad remapQuadUVToRange(
                                                BakedQuad sourceQuad,
                                                BakedQuad targetQuad,
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

    @Nullable
    private static BakedQuad getQuad(BakedModel model, Direction direction) {
        List<BakedQuad> quads = model.getQuads(null, direction, RandomSource.create());
        return quads.isEmpty() ? null : quads.getFirst();
    }

    private static class BakedPipeModel implements BakedModel {

        private final List<BakedQuad> unculledFaces = new ArrayList<>();
        private final Map<Direction, List<BakedQuad>> culledFaces = new EnumMap<>(Direction.class);
        protected final TextureAtlasSprite particleIcon;

        private BakedPipeModel(BakedModel base, BakedModel basePipe) {
            for (Direction direction : Direction.values()) {
                BakedQuad quad = getQuad(basePipe, direction);
                if (quad == null) {
                    culledFaces.put(direction, new ArrayList<>());
                    continue;
                }

                BakedQuad quadBase = getQuad(base, direction);
                BakedQuad newQuad = remapQuadUVToRange(quad, quadBase, 5, 11, 5, 11);
                culledFaces.put(direction, List.of(newQuad));
            }

            for (BakedQuad quad : basePipe.getQuads(null, null, RandomSource.create())) {
                Direction direction = quad.getDirection();
                BakedQuad quadBase = getQuad(base, direction);

                // Calculate face positions of each vertex when the face is full-sized (16*16)
                int[] vertices = quad.getVertices();
                float[] facePositions = new float[4];
                for (int i = 0; i < 4; i++) {
                    int vertexIndex = i * 8;
                    float x = Float.intBitsToFloat(vertices[vertexIndex]);
                    float y = Float.intBitsToFloat(vertices[vertexIndex + 1]);
                    float z = Float.intBitsToFloat(vertices[vertexIndex + 2]);

                    // Calculate face coordinates based on direction (range: 0-16)
                    float facePos;
                    switch (direction) {
                        case NORTH, SOUTH -> facePos = x * 16.0f; // Use x coordinate
                        case EAST, WEST -> facePos = z * 16.0f; // Use z coordinate
                        case UP, DOWN -> facePos = x * 16.0f; // Use x coordinate
                        default -> facePos = 0.0f;
                    }
                    facePositions[i] = facePos;
                }
                System.out.println("Direction: " + direction + ", Face positions: " +
                        facePositions[0] + ", " + facePositions[1] + ", " +
                        facePositions[2] + ", " + facePositions[3]);

                BakedQuad newQuad = remapQuadUVToRange(quad, quadBase, facePositions[0], facePositions[2],
                        facePositions[1], facePositions[3]);
                unculledFaces.add(newQuad);
            }

            this.particleIcon = base.getParticleIcon();
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction,
                                        RandomSource randomSource) {
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
        public TextureAtlasSprite getParticleIcon() {
            return this.particleIcon;
        }

        @Override
        public ItemOverrides getOverrides() {
            return ItemOverrides.EMPTY;
        }
    }

    private static BakedModel rotateModel(@NotNull BlockModel model, @NotNull ModelBaker baker,
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

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        this.base.resolveParents(modelGetter);
        if (this.overlay != null) {
            this.overlay.resolveParents(modelGetter);
        }
    }
}
