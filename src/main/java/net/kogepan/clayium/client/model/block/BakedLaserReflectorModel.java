package net.kogepan.clayium.client.model.block;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;

import com.mojang.math.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Baked model for the Laser Reflector pyramid (base quad + 4 triangular faces).
 * Geometry matches {@link net.kogepan.clayium.client.renderer.LaserReflectorRenderer}.
 */
@OnlyIn(Dist.CLIENT)
public class BakedLaserReflectorModel implements BakedModel {

    private static final float F = 0.125f;

    private final List<BakedQuad> quads;
    private final TextureAtlasSprite sprite;

    public BakedLaserReflectorModel(TextureAtlasSprite sprite, Transformation rotation) {
        this.sprite = sprite;
        this.quads = buildPyramidQuads(sprite, rotation);
    }

    @Override
    @NotNull
    public List<BakedQuad> getQuads(@Nullable BlockState state,
                                    @Nullable Direction side,
                                    @NotNull RandomSource random, @NotNull ModelData data,
                                    @Nullable net.minecraft.client.renderer.RenderType renderType) {
        // Return quads when chunk asks for our declared render type (translucent) or when renderType is null
        if (renderType != null && renderType != RenderType.translucent()) {
            return List.of();
        }
        if (side != null) {
            return List.of();
        }
        return quads;
    }

    @Override
    @NotNull
    public List<BakedQuad> getQuads(@Nullable BlockState state,
                                    @Nullable Direction side, @NotNull RandomSource random) {
        return getQuads(state, side, random, ModelData.EMPTY, null);
    }

    @Override
    @NotNull
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource random,
                                             @NotNull ModelData data) {
        return ChunkRenderTypeSet.of(RenderType.translucent());
    }

    @Override
    @NotNull
    public TextureAtlasSprite getParticleIcon() {
        return sprite;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    @NotNull
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    private static List<BakedQuad> buildPyramidQuads(TextureAtlasSprite sprite, Transformation rotation) {
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        float x0 = F * 2;
        float x1 = 1 - F * 2;
        float z0 = F * 2;
        float z1 = 1 - F * 2;
        float yBase = F;
        float apexX = 0.5f;
        float apexY = 1 - F;
        float apexZ = 0.5f;

        Matrix4f matrix = rotation.getMatrix();
        Vector3f center = new Vector3f(0.5f, 0.5f, 0.5f);

        List<BakedQuad> out = new ArrayList<>(5);

        // Base quad (y = yBase, normal DOWN)
        Vector3f[] baseVerts = {
                transform(matrix, center, x0, yBase, z0),
                transform(matrix, center, x1, yBase, z0),
                transform(matrix, center, x1, yBase, z1),
                transform(matrix, center, x0, yBase, z1)
        };
        float[][] baseUV = { { u0, v0 }, { u1, v0 }, { u1, v1 }, { u0, v1 } };
        out.add(createQuad(baseVerts, baseUV, Direction.DOWN, sprite));

        // 4 triangular faces (each as quad with duplicate first vertex). UVs match original renderer.
        Vector3f apex = transform(matrix, center, apexX, apexY, apexZ);
        Vector3f[][] faceVerts = {
                { apex, transform(matrix, center, x1, yBase, z0), transform(matrix, center, x0, yBase, z0) },
                { apex, transform(matrix, center, x1, yBase, z1), transform(matrix, center, x1, yBase, z0) },
                { apex, transform(matrix, center, x0, yBase, z1), transform(matrix, center, x1, yBase, z1) },
                { apex, transform(matrix, center, x0, yBase, z0), transform(matrix, center, x0, yBase, z1) }
        };
        float[][] faceUV = { { u1, v0 }, { u1, v1 }, { u0, v1 }, { u1, v0 } };
        Direction[] faceDirs = {
                directionFromNormal(0.5f, 1, 0),
                directionFromNormal(1, 1, 0.5f),
                directionFromNormal(0.5f, 1, 1),
                directionFromNormal(0, 1, 0.5f)
        };
        for (int i = 0; i < 4; i++) {
            Vector3f[] tri = faceVerts[i];
            Vector3f[] quad = new Vector3f[] { tri[0], tri[1], tri[2], tri[0] };
            out.add(createQuad(quad, faceUV, faceDirs[i], sprite));
        }

        return out;
    }

    private static Vector3f transform(Matrix4f matrix, Vector3f center, float x, float y, float z) {
        Vector4f v = new Vector4f(x - center.x(), y - center.y(), z - center.z(), 1f);
        v.mul(matrix);
        return new Vector3f(v.x() + center.x(), v.y() + center.y(), v.z() + center.z());
    }

    private static Direction directionFromNormal(float nx, float ny, float nz) {
        float ax = Math.abs(nx);
        float ay = Math.abs(ny);
        float az = Math.abs(nz);
        if (ay >= ax && ay >= az) return ny > 0 ? Direction.UP : Direction.DOWN;
        if (ax >= az) return nx > 0 ? Direction.EAST : Direction.WEST;
        return nz > 0 ? Direction.SOUTH : Direction.NORTH;
    }

    /**
     * Creates a BakedQuad from 4 vertices and UVs. Vertex format: 8 ints per vertex
     * (x,y,z, color, u, v, light, overlay).
     */
    private static BakedQuad createQuad(Vector3f[] verts, float[][] uv, Direction cullFace,
                                        TextureAtlasSprite sprite) {
        int[] vertexData = new int[32];
        int white = (225 << 24) | 0x00_FF_FF_FF; // ARGB, alpha 225 to match original renderer
        for (int i = 0; i < 4; i++) {
            int o = i * 8;
            vertexData[o] = Float.floatToRawIntBits(verts[i].x());
            vertexData[o + 1] = Float.floatToRawIntBits(verts[i].y());
            vertexData[o + 2] = Float.floatToRawIntBits(verts[i].z());
            vertexData[o + 3] = white;
            // UV already in atlas 0-1 space (from sprite.getU0/getV0 etc.)
            vertexData[o + 4] = Float.floatToRawIntBits(uv[i][0]);
            vertexData[o + 5] = Float.floatToRawIntBits(uv[i][1]);
            vertexData[o + 6] = 0;
            vertexData[o + 7] = 0;
        }
        net.neoforged.neoforge.client.ClientHooks.fillNormal(vertexData, cullFace);
        return new BakedQuad(vertexData, -1, cullFace, sprite, false);
    }
}
