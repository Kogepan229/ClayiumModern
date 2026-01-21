package net.kogepan.clayium.client.model;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.client.utils.StaticFaceBakery;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class PipeOverlayQuads {

    @Getter
    private static boolean isInitialized = false;

    // MachineIOMode -> List of BakedQuads (one for each direction)
    private static final Map<MachineIOMode, List<BakedQuad>> IMPORT_MODE_QUADS = new EnumMap<>(MachineIOMode.class);
    private static final Map<MachineIOMode, List<BakedQuad>> EXPORT_MODE_QUADS = new EnumMap<>(MachineIOMode.class);

    // Filter quads for each direction
    private static final List<BakedQuad> FILTER_QUADS = new ArrayList<>();

    /**
     * Creates a BakedQuad for a specific face with custom UV coordinates.
     *
     * @param side    The direction of the face
     * @param texture The texture sprite
     * @param uv      UV coordinates [uMin, vMin, uMax, vMax]
     * @return The created BakedQuad
     */
    @NotNull
    public static BakedQuad createQuad(@NotNull Direction side, @NotNull TextureAtlasSprite texture,
                                       float[] uv) {
        return StaticFaceBakery.bakeQuad(
                new Vector3f(0f, 0f, 0f),
                new Vector3f(16f, 16f, 16f),
                new BlockElementFace(null, -1, "", new BlockFaceUV(uv, 0)),
                texture,
                side,
                BlockModelRotation.X0_Y0,
                null,
                true,
                0);
    }

    /**
     * Creates a BakedQuad for a specific face with default UV coordinates (0, 0, 16, 16).
     *
     * @param side    The direction of the face
     * @param texture The texture sprite
     * @return The created BakedQuad
     */
    @NotNull
    public static BakedQuad createQuad(@NotNull Direction side, @NotNull TextureAtlasSprite texture) {
        return createQuad(side, texture, new float[] { 0f, 0f, 16f, 16f });
    }

    /**
     * Creates a BakedQuad for a specific face with custom position and size.
     *
     * @param side    The direction of the face
     * @param texture The texture sprite
     * @param from    Starting position (in block coordinates, 0-1 range)
     * @param to      Ending position (in block coordinates, 0-1 range)
     * @param uv      UV coordinates [uMin, vMin, uMax, vMax]
     * @return The created BakedQuad
     */
    @NotNull
    public static BakedQuad createQuad(@NotNull Direction side, @NotNull TextureAtlasSprite texture,
                                       @NotNull Vector3f from, @NotNull Vector3f to, float[] uv) {
        return StaticFaceBakery.bakeQuad(
                from,
                to,
                new BlockElementFace(null, -1, "", new BlockFaceUV(uv, 0)),
                texture,
                side,
                BlockModelRotation.X0_Y0,
                null,
                true,
                0);
    }

    /**
     * Initializes all BakedQuads. This should be called when textures are stitched.
     *
     * @param atlas The texture atlas
     */
    public static void initialize(@NotNull TextureAtlas atlas) {
        isInitialized = true;

        // Initialize import mode quads
        for (MachineIOMode mode : MachineIOMode.values()) {
            List<BakedQuad> quads = new ArrayList<>();
            ResourceLocation textureLoc = ModelTextures.getOverlayImportPipe(mode);
            if (textureLoc != null) {
                TextureAtlasSprite sprite = atlas.getSprite(textureLoc);
                for (Direction side : Direction.values()) {
                    quads.add(createQuad(side, sprite));
                }
            }
            IMPORT_MODE_QUADS.put(mode, quads);
        }

        // Initialize export mode quads
        for (MachineIOMode mode : MachineIOMode.values()) {
            List<BakedQuad> quads = new ArrayList<>();
            ResourceLocation textureLoc = ModelTextures.getOverlayExportPipe(mode);
            if (textureLoc != null) {
                TextureAtlasSprite sprite = atlas.getSprite(textureLoc);
                for (Direction side : Direction.values()) {
                    quads.add(createQuad(side, sprite));
                }
            }
            EXPORT_MODE_QUADS.put(mode, quads);
        }

        // Initialize filter quads
        ResourceLocation filterTexture = Clayium.id("block/filter");
        TextureAtlasSprite filterSprite = atlas.getSprite(filterTexture);
        FILTER_QUADS.clear();
        for (Direction side : Direction.values()) {
            FILTER_QUADS.add(createQuad(side, filterSprite));
        }
    }

    /**
     * Gets the import quad for a specific MachineIOMode and direction.
     *
     * @param mode The MachineIOMode
     * @param side The direction
     * @return The BakedQuad, or null if not available
     */
    @Nullable
    public static BakedQuad getImportQuad(@NotNull MachineIOMode mode, @NotNull Direction side) {
        List<BakedQuad> quads = IMPORT_MODE_QUADS.get(mode);
        if (quads == null || quads.isEmpty()) {
            return null;
        }
        return quads.get(side.ordinal());
    }

    /**
     * Gets the export quad for a specific MachineIOMode and direction.
     *
     * @param mode The MachineIOMode
     * @param side The direction
     * @return The BakedQuad, or null if not available
     */
    @Nullable
    public static BakedQuad getExportQuad(@NotNull MachineIOMode mode, @NotNull Direction side) {
        List<BakedQuad> quads = EXPORT_MODE_QUADS.get(mode);
        if (quads == null || quads.isEmpty()) {
            return null;
        }
        return quads.get(side.ordinal());
    }
}
