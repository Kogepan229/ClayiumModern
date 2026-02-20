package net.kogepan.clayium.client.utils;

import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.model.ExtraFaceData;
import net.neoforged.neoforge.client.model.QuadTransformers;

import com.mojang.math.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class StaticFaceBakery {

    private static final float RESCALE_22_5 = 1.0F / (float) Math.cos((float) (Math.PI / 8)) - 1.0F;
    private static final float RESCALE_45 = 1.0F / (float) Math.cos((float) (Math.PI / 4)) - 1.0F;

    /**
     * bake a quad of specific face.
     * 
     * @param cube       cube model
     * @param face       face of the quad
     * @param sprite     texture
     * @param rotation   additional rotation
     * @param tintIndex  tint color index
     * @param emissivity emissivity
     * @param cull       whether cull the face
     * @param shade      whether shade the face
     */
    public static BakedQuad bakeFace(AABB cube, Direction face, TextureAtlasSprite sprite, ModelState rotation,
                                     int tintIndex, int emissivity, boolean cull, boolean shade) {
        return bakeQuad(
                new Vector3f((float) cube.minX * 16f, (float) cube.minY * 16f, (float) cube.minZ * 16f),
                new Vector3f((float) cube.maxX * 16f, (float) cube.maxY * 16f, (float) cube.maxZ * 16f),
                new BlockElementFace(cull ? face : null, tintIndex, "",
                        new BlockFaceUV(new float[] { 0.0F, 0.0F, 16.0F, 16.0F }, 0)),
                sprite,
                face,
                rotation,
                null,
                shade,
                emissivity);
    }

    public static BakedQuad bakeFace(AABB cube, Direction face, TextureAtlasSprite sprite) {
        return bakeFace(cube, face, sprite, BlockModelRotation.X0_Y0, -1, 0, true, true);
    }

    public static BakedQuad bakeQuad(Vector3f posFrom, Vector3f posTo, BlockElementFace face, TextureAtlasSprite sprite,
                                     Direction facing, ModelState transform, @Nullable BlockElementRotation rotation,
                                     boolean shade, int emissivity) {
        BlockFaceUV blockfaceuv = face.uv();
        if (transform.isUvLocked()) {
            blockfaceuv = FaceBakery.recomputeUVs(face.uv(), facing, transform.getRotation());
        }

        float[] afloat = new float[blockfaceuv.uvs.length];
        System.arraycopy(blockfaceuv.uvs, 0, afloat, 0, afloat.length);
        float f = sprite.uvShrinkRatio();
        float f1 = (blockfaceuv.uvs[0] + blockfaceuv.uvs[0] + blockfaceuv.uvs[2] + blockfaceuv.uvs[2]) / 4.0F;
        float f2 = (blockfaceuv.uvs[1] + blockfaceuv.uvs[1] + blockfaceuv.uvs[3] + blockfaceuv.uvs[3]) / 4.0F;
        blockfaceuv.uvs[0] = Mth.lerp(f, blockfaceuv.uvs[0], f1);
        blockfaceuv.uvs[2] = Mth.lerp(f, blockfaceuv.uvs[2], f1);
        blockfaceuv.uvs[1] = Mth.lerp(f, blockfaceuv.uvs[1], f2);
        blockfaceuv.uvs[3] = Mth.lerp(f, blockfaceuv.uvs[3], f2);
        int[] aint = makeVertices(blockfaceuv, sprite, facing, setupShape(posFrom, posTo), transform.getRotation(),
                rotation, shade);
        Direction direction = FaceBakery.calculateFacing(aint);
        System.arraycopy(afloat, 0, blockfaceuv.uvs, 0, afloat.length);
        if (rotation == null && !transform.mayApplyArbitraryRotation()) {
            recalculateWinding(aint, direction);
        }

        ClientHooks.fillNormal(aint, direction);
        ExtraFaceData data = face.faceData();
        BakedQuad quad = new BakedQuad(aint, face.tintIndex(), direction, sprite, shade, data.ambientOcclusion());
        if (!ExtraFaceData.DEFAULT.equals(data)) {
            QuadTransformers.applyingLightmap(data.blockLight(), data.skyLight()).processInPlace(quad);
            QuadTransformers.applyingColor(data.color()).processInPlace(quad);
        }
        QuadTransformers.settingEmissivity(emissivity).processInPlace(quad);

        return quad;
    }

    private static int[] makeVertices(BlockFaceUV uvs, TextureAtlasSprite sprite, Direction orientation,
                                      float[] posDiv16, Transformation rotation,
                                      @Nullable BlockElementRotation partRotation, boolean shade) {
        int[] aint = new int[32];

        for (int i = 0; i < 4; ++i) {
            bakeVertex(aint, i, orientation, uvs, posDiv16, sprite, rotation, partRotation, shade);
        }

        return aint;
    }

    private static float[] setupShape(Vector3f min, Vector3f max) {
        float[] afloat = new float[Direction.values().length];
        afloat[FaceInfo.Constants.MIN_X] = min.x() / 16.0F;
        afloat[FaceInfo.Constants.MIN_Y] = min.y() / 16.0F;
        afloat[FaceInfo.Constants.MIN_Z] = min.z() / 16.0F;
        afloat[FaceInfo.Constants.MAX_X] = max.x() / 16.0F;
        afloat[FaceInfo.Constants.MAX_Y] = max.y() / 16.0F;
        afloat[FaceInfo.Constants.MAX_Z] = max.z() / 16.0F;
        return afloat;
    }

    private static void bakeVertex(int[] vertexData, int vertexIndex, Direction facing, BlockFaceUV blockFaceUV,
                                   float[] posDiv16, TextureAtlasSprite sprite, Transformation rotation,
                                   @Nullable BlockElementRotation partRotation, boolean shade) {
        FaceInfo.VertexInfo faceinfo$vertexinfo = FaceInfo.fromFacing(facing).getVertexInfo(vertexIndex);
        Vector3f vector3f = new Vector3f(posDiv16[faceinfo$vertexinfo.xFace], posDiv16[faceinfo$vertexinfo.yFace],
                posDiv16[faceinfo$vertexinfo.zFace]);
        applyElementRotation(vector3f, partRotation);
        applyModelRotation(vector3f, rotation);
        fillVertex(vertexData, vertexIndex, vector3f, sprite, blockFaceUV);
    }

    private static void fillVertex(int[] vertexData, int vertexIndex, Vector3f vector, TextureAtlasSprite sprite,
                                   BlockFaceUV blockFaceUV) {
        final int i = vertexIndex * 8;
        vertexData[i] = Float.floatToRawIntBits(vector.x());
        vertexData[i + 1] = Float.floatToRawIntBits(vector.y());
        vertexData[i + 2] = Float.floatToRawIntBits(vector.z());
        vertexData[i + 3] = -1;
        vertexData[i + 4] = Float.floatToRawIntBits(sprite.getU(blockFaceUV.getU(vertexIndex) / 16.0F));
        vertexData[i + 4 + 1] = Float.floatToRawIntBits(sprite.getV(blockFaceUV.getV(vertexIndex) / 16.0F));
    }

    private static void applyElementRotation(Vector3f vec, @Nullable BlockElementRotation partRotation) {
        if (partRotation != null) {
            Vector3f vector3f;
            Vector3f vector3f1;
            switch (partRotation.axis()) {
                case X:
                    vector3f = new Vector3f(1.0F, 0.0F, 0.0F);
                    vector3f1 = new Vector3f(0.0F, 1.0F, 1.0F);
                    break;
                case Y:
                    vector3f = new Vector3f(0.0F, 1.0F, 0.0F);
                    vector3f1 = new Vector3f(1.0F, 0.0F, 1.0F);
                    break;
                case Z:
                    vector3f = new Vector3f(0.0F, 0.0F, 1.0F);
                    vector3f1 = new Vector3f(1.0F, 1.0F, 0.0F);
                    break;
                default:
                    throw new IllegalArgumentException("There are only 3 axes");
            }

            Quaternionf quaternionf = (new Quaternionf()).rotationAxis(partRotation.angle() * ((float) Math.PI / 180F),
                    vector3f);
            if (partRotation.rescale()) {
                if (Math.abs(partRotation.angle()) == 22.5F) {
                    vector3f1.mul(RESCALE_22_5);
                } else {
                    vector3f1.mul(RESCALE_45);
                }

                vector3f1.add(1.0F, 1.0F, 1.0F);
            } else {
                vector3f1.set(1.0F, 1.0F, 1.0F);
            }

            rotateVertexBy(vec, new Vector3f(partRotation.origin()), (new Matrix4f()).rotation(quaternionf), vector3f1);
        }
    }

    public static void applyModelRotation(Vector3f pos, Transformation transform) {
        if (transform != Transformation.identity()) {
            rotateVertexBy(pos, new Vector3f(0.5F, 0.5F, 0.5F), transform.getMatrix(), new Vector3f(1.0F, 1.0F, 1.0F));
        }
    }

    private static void rotateVertexBy(Vector3f pos, Vector3f origin, Matrix4f transform, Vector3f scale) {
        Vector4f vector4f = transform
                .transform(new Vector4f(pos.x() - origin.x(), pos.y() - origin.y(), pos.z() - origin.z(), 1.0F));
        vector4f.mul(new Vector4f(scale, 1.0F));
        pos.set(vector4f.x() + origin.x(), vector4f.y() + origin.y(), vector4f.z() + origin.z());
    }

    private static void recalculateWinding(int[] vertices, Direction direction) {
        int[] aint = new int[vertices.length];
        System.arraycopy(vertices, 0, aint, 0, vertices.length);
        float[] afloat = new float[Direction.values().length];
        afloat[FaceInfo.Constants.MIN_X] = 999.0F;
        afloat[FaceInfo.Constants.MIN_Y] = 999.0F;
        afloat[FaceInfo.Constants.MIN_Z] = 999.0F;
        afloat[FaceInfo.Constants.MAX_X] = -999.0F;
        afloat[FaceInfo.Constants.MAX_Y] = -999.0F;
        afloat[FaceInfo.Constants.MAX_Z] = -999.0F;

        for (int i = 0; i < 4; ++i) {
            int j = 8 * i;
            float f = Float.intBitsToFloat(aint[j]);
            float f1 = Float.intBitsToFloat(aint[j + 1]);
            float f2 = Float.intBitsToFloat(aint[j + 2]);
            if (f < afloat[FaceInfo.Constants.MIN_X]) {
                afloat[FaceInfo.Constants.MIN_X] = f;
            }

            if (f1 < afloat[FaceInfo.Constants.MIN_Y]) {
                afloat[FaceInfo.Constants.MIN_Y] = f1;
            }

            if (f2 < afloat[FaceInfo.Constants.MIN_Z]) {
                afloat[FaceInfo.Constants.MIN_Z] = f2;
            }

            if (f > afloat[FaceInfo.Constants.MAX_X]) {
                afloat[FaceInfo.Constants.MAX_X] = f;
            }

            if (f1 > afloat[FaceInfo.Constants.MAX_Y]) {
                afloat[FaceInfo.Constants.MAX_Y] = f1;
            }

            if (f2 > afloat[FaceInfo.Constants.MAX_Z]) {
                afloat[FaceInfo.Constants.MAX_Z] = f2;
            }
        }

        FaceInfo faceinfo = FaceInfo.fromFacing(direction);

        for (int i1 = 0; i1 < 4; ++i1) {
            int j1 = 8 * i1;
            FaceInfo.VertexInfo faceinfo$vertexinfo = faceinfo.getVertexInfo(i1);
            float f8 = afloat[faceinfo$vertexinfo.xFace];
            float f3 = afloat[faceinfo$vertexinfo.yFace];
            float f4 = afloat[faceinfo$vertexinfo.zFace];
            vertices[j1] = Float.floatToRawIntBits(f8);
            vertices[j1 + 1] = Float.floatToRawIntBits(f3);
            vertices[j1 + 2] = Float.floatToRawIntBits(f4);

            for (int k = 0; k < 4; ++k) {
                int l = 8 * k;
                float f5 = Float.intBitsToFloat(aint[l]);
                float f6 = Float.intBitsToFloat(aint[l + 1]);
                float f7 = Float.intBitsToFloat(aint[l + 2]);
                if (Mth.equal(f8, f5) && Mth.equal(f3, f6) && Mth.equal(f4, f7)) {
                    vertices[j1 + 4] = aint[l + 4];
                    vertices[j1 + 4 + 1] = aint[l + 4 + 1];
                }
            }
        }
    }
}
