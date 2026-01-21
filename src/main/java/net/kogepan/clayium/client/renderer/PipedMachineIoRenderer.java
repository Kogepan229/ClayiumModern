package net.kogepan.clayium.client.renderer;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blocks.TestClayContainerBlock;
import net.kogepan.clayium.client.model.PipeOverlayQuads;
import net.kogepan.clayium.client.utils.ModelUtils;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class PipedMachineIoRenderer implements BlockEntityRenderer<ClayContainerBlockEntity> {

    // Note: z-fighting offset is now applied uniformly in ModelUtils.createSideCubeQuads
    // (same approach as ClayiumUnofficial), so individual face offsets are no longer needed

    public PipedMachineIoRenderer(BlockEntityRendererProvider.Context context) {
        // Context can be used to get render dispatcher, item renderer, etc. if needed
    }

    @Override
    public void render(@NotNull ClayContainerBlockEntity blockEntity, float partialTick,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack heldItem = player.getMainHandItem();
        if (!isPipingTool(heldItem)) return;

        var state = blockEntity.getBlockState();
        if (!state.getValue(TestClayContainerBlock.PIPE)) {
            return;
        }

        poseStack.pushPose();

        // Render IO icons for each connected side
        for (Direction side : Direction.values()) {
            // Check if this side is connected
            var property = TestClayContainerBlock.getProperty(side);
            if (!state.getValue(property)) continue;

            MachineIOMode importMode = blockEntity.getInputMode(side);
            MachineIOMode exportMode = blockEntity.getOutputMode(side);

            // Use BakedQuad from ModelTextures
            BakedQuad importQuad = PipeOverlayQuads.getImportQuad(importMode, side);
            BakedQuad exportQuad = PipeOverlayQuads.getExportQuad(exportMode, side);

            if (importQuad != null) {
                renderSideIconWithBakedQuad(poseStack, buffer, side, importQuad, packedLight, packedOverlay);
            }
            if (exportQuad != null) {
                renderSideIconWithBakedQuad(poseStack, buffer, side, exportQuad, packedLight, packedOverlay);
            }
        }

        poseStack.popPose();
    }

    /**
     * Renders a side icon using a BakedQuad.
     * Creates side cube quads for the pipe arm and renders them with the overlay texture.
     *
     * @param poseStack     The pose stack
     * @param buffer        The buffer source
     * @param side          The side/direction of the pipe arm
     * @param overlayQuad   The overlay BakedQuad to render (contains texture info)
     * @param packedLight   Packed light value
     * @param packedOverlay Packed overlay value
     */
    private void renderSideIconWithBakedQuad(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                                             @NotNull Direction side, @NotNull BakedQuad overlayQuad,
                                             int packedLight, int packedOverlay) {
        if (!PipeOverlayQuads.isInitialized()) {
            return;
        }

        poseStack.pushPose();

        // Get the side cube quads for this side (geometry)
        TextureAtlasSprite overlaySprite = overlayQuad.getSprite();
        var sideCubeQuads = createSideCubeQuads(overlaySprite);
        List<BakedQuad> cubeQuads = sideCubeQuads.get(side.ordinal());

        // Get the render type for the sprite
        RenderType renderType = RenderType.entityCutout(overlaySprite.atlasLocation());

        VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
        PoseStack.Pose pose = poseStack.last();

        // Render all quads of the side cube with overlay texture
        // Note: z-fighting offset is already applied in ModelUtils.createSideCubeQuads
        for (BakedQuad baseQuad : cubeQuads) {
            // Create a new quad with the base geometry but overlay texture
            BakedQuad overlayQuadForFace = createOverlayQuad(baseQuad, overlayQuad);
            // Render the quad by writing vertices directly to VertexConsumer
            renderBakedQuad(vertexConsumer, pose, overlayQuadForFace, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    /**
     * Creates an overlay quad by combining the base quad geometry with the overlay texture.
     *
     * @param baseQuad    The base quad (defines geometry and UV mapping)
     * @param overlayQuad The overlay quad (defines texture sprite)
     * @return A new BakedQuad with base geometry and overlay texture
     */
    @NotNull
    private BakedQuad createOverlayQuad(@NotNull BakedQuad baseQuad, @NotNull BakedQuad overlayQuad) {
        int[] vertices = baseQuad.getVertices().clone();
        TextureAtlasSprite overlaySprite = overlayQuad.getSprite();

        // The base quad already has the correct UV coordinates for the geometry
        // We just need to ensure the sprite is set correctly
        // The UV coordinates in the base quad are already correct for the overlay sprite
        // since both use the same texture atlas

        return new BakedQuad(
                vertices,
                baseQuad.getTintIndex(),
                baseQuad.getDirection(),
                overlaySprite,
                baseQuad.isShade());
    }

    /**
     * Renders a BakedQuad by writing its vertices directly to a VertexConsumer.
     * Note: z-fighting offset is now applied uniformly in ModelUtils.createSideCubeQuads,
     * so individual face offsets are no longer applied here.
     *
     * @param vertexConsumer The vertex consumer to write to
     * @param pose           The pose from the pose stack
     * @param quad           The BakedQuad to render
     * @param packedLight    Packed light value
     * @param packedOverlay  Packed overlay value
     */
    private void renderBakedQuad(@NotNull VertexConsumer vertexConsumer, @NotNull PoseStack.Pose pose,
                                 @NotNull BakedQuad quad, int packedLight, int packedOverlay) {
        int[] vertices = quad.getVertices();
        Direction direction = quad.getDirection();

        // Extract normal vector from direction (this is the face normal)
        float nx = direction.getStepX();
        float ny = direction.getStepY();
        float nz = direction.getStepZ();

        // Transform normal by pose normal matrix
        org.joml.Vector3f normal = new org.joml.Vector3f(nx, ny, nz);
        normal.mul(pose.normal());
        nx = normal.x();
        ny = normal.y();
        nz = normal.z();

        // Write each vertex (4 vertices per quad)
        for (int i = 0; i < 4; i++) {
            int vertexOffset = i * 8;

            // Extract position (x, y, z)
            // Note: z-fighting offset is already applied in ModelUtils.createSideCubeQuads
            float x = Float.intBitsToFloat(vertices[vertexOffset]);
            float y = Float.intBitsToFloat(vertices[vertexOffset + 1]);
            float z = Float.intBitsToFloat(vertices[vertexOffset + 2]);

            // Extract color (ARGB)
            int color = vertices[vertexOffset + 3];
            int a = (color >> 24) & 0xFF;
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;

            // Extract UV coordinates
            float u = Float.intBitsToFloat(vertices[vertexOffset + 4]);
            float v = Float.intBitsToFloat(vertices[vertexOffset + 5]);

            // Extract lightmap (use provided packedLight if available, otherwise use from vertex)
            int light = vertices[vertexOffset + 6];
            if (packedLight != 0) {
                light = packedLight;
            }

            // Extract overlay (use provided packedOverlay if available, otherwise use from vertex)
            int overlay = vertices[vertexOffset + 7];
            if (packedOverlay != 0) {
                overlay = packedOverlay;
            }

            // Transform position by pose matrix
            org.joml.Vector4f pos = new org.joml.Vector4f(x, y, z, 1.0f);
            pos.mul(pose.pose());
            x = pos.x();
            y = pos.y();
            z = pos.z();

            // Write vertex to consumer
            vertexConsumer.addVertex(x, y, z)
                    .setColor(r, g, b, a)
                    .setUv(u, v)
                    .setOverlay(overlay)
                    .setLight(light)
                    .setNormal(nx, ny, nz);
        }
    }

    private boolean isPipingTool(@NotNull ItemStack stack) {
        // For now, check for CLAY_SPATULA (piping tool)
        // In the future, you might want to add CLAY_PIPING_TOOL and CLAY_IO_CONFIGURATOR
        return stack.is(net.kogepan.clayium.registries.ClayiumItems.CLAY_SPATULA.get());
    }

    // offset to prevent z-fighting
    private static final float CUBE_OFFSET = 0.01f;

    /**
     * Side cube positions for pipe arms.
     * From top to bottom: DOWN, UP, NORTH, SOUTH, WEST, EAST
     * Note: These are base positions without offset. Offset is applied in createSideCubeQuads.
     */
    private static final List<ModelUtils.Pair<Vector3f, Vector3f>> SIDE_CUBES = List.of(
            new ModelUtils.Pair<>(new Vector3f(5f, 0f, 5f), new Vector3f(11f, 5f, 11f)),      // DOWN
            new ModelUtils.Pair<>(new Vector3f(5f, 11f, 5f), new Vector3f(11f, 16f, 11f)),    // UP
            new ModelUtils.Pair<>(new Vector3f(5f, 5f, 0f), new Vector3f(11f, 11f, 5f)),      // NORTH
            new ModelUtils.Pair<>(new Vector3f(5f, 5f, 11f), new Vector3f(11f, 11f, 16f)),    // SOUTH
            new ModelUtils.Pair<>(new Vector3f(0f, 5f, 5f), new Vector3f(5f, 11f, 11f)),      // WEST
            new ModelUtils.Pair<>(new Vector3f(11f, 5f, 5f), new Vector3f(16f, 11f, 11f))     // EAST
    );

    /**
     * Creates BakedQuads for side cubes (pipe arms).
     * Based on ClayiumUnofficial's ModelUtils.createSideCubeQuads method.
     * Applies uniform offset to prevent z-fighting (same approach as ClayiumUnofficial).
     *
     * @param texture The texture sprite to use
     * @return List of lists of BakedQuads, one list per cube position
     */
    @NotNull
    public static List<List<BakedQuad>> createSideCubeQuads(@NotNull TextureAtlasSprite texture) {
        List<List<BakedQuad>> result = new ArrayList<>();
        for (int i = 0; i < SIDE_CUBES.size(); i++) {
            ModelUtils.Pair<Vector3f, Vector3f> baseCube = SIDE_CUBES.get(i);
            Direction cubePos = Direction.values()[i];

            // Apply uniform offset to prevent z-fighting (same as ClayiumUnofficial)
            // Start coordinates: subtract CUBE_OFFSET
            // End coordinates: add CUBE_OFFSET * 2
            Vector3f from = new Vector3f(
                    baseCube.first.x() - CUBE_OFFSET,
                    baseCube.first.y() - CUBE_OFFSET,
                    baseCube.first.z() - CUBE_OFFSET);
            Vector3f to = new Vector3f(
                    baseCube.second.x() + CUBE_OFFSET * 2,
                    baseCube.second.y() + CUBE_OFFSET * 2,
                    baseCube.second.z() + CUBE_OFFSET * 2);

            List<BakedQuad> quads = new ArrayList<>();

            // Create quads for all visible faces of this cube
            for (Direction face : Direction.values()) {
                if (face == cubePos.getOpposite()) continue; // Skip the hidden face
                float[] uv = ModelUtils.getUvFloat(cubePos, face);
                quads.add(PipeOverlayQuads.createQuad(face, texture, from, to, uv));
            }

            result.add(quads);
        }
        return result;
    }
}
