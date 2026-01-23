package net.kogepan.clayium.client.model;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class ModelTextures {

    private static final ResourceLocation OVERLAY_IMPORT_PIPE_ALL = Clayium.id("block/overlay/import_pipe_all");
    private static final ResourceLocation OVERLAY_IMPORT_PIPE_CE = Clayium.id("block/overlay/import_pipe_ce");
    private static final ResourceLocation OVERLAY_EXPORT_PIPE_ALL = Clayium.id("block/overlay/export_pipe_all");

    private static final ResourceLocation OVERLAY_IMPORT_ALL = Clayium.id("block/overlay/import_all");
    private static final ResourceLocation OVERLAY_IMPORT_CE = Clayium.id("block/overlay/import_ce");
    private static final ResourceLocation OVERLAY_EXPORT_ALL = Clayium.id("block/overlay/export_all");

    private static final Map<MachineIOMode, @Nullable TextureAtlasSprite> OVERLAY_IMPORT_SPRITES = new EnumMap<>(
            MachineIOMode.class);
    private static final Map<MachineIOMode, @Nullable TextureAtlasSprite> OVERLAY_EXPORT_SPRITES = new EnumMap<>(
            MachineIOMode.class);

    @Nullable
    public static ResourceLocation getOverlayImport(MachineIOMode mode) {
        return switch (mode) {
            case NONE -> null;
            case FIRST -> null;
            case SECOND -> null;
            case ALL -> OVERLAY_IMPORT_ALL;
            case CE -> OVERLAY_IMPORT_CE;
        };
    }

    @Nullable
    public static ResourceLocation getOverlayExport(MachineIOMode mode) {
        return switch (mode) {
            case NONE -> null;
            case FIRST -> null;
            case SECOND -> null;
            case ALL -> OVERLAY_EXPORT_ALL;
            case CE -> null;
        };
    }

    @Nullable
    public static ResourceLocation getOverlayImportPipe(MachineIOMode mode) {
        return switch (mode) {
            case NONE -> null;
            case FIRST -> null;
            case SECOND -> null;
            case ALL -> OVERLAY_IMPORT_PIPE_ALL;
            case CE -> OVERLAY_IMPORT_PIPE_CE;
        };
    }

    @Nullable
    public static ResourceLocation getOverlayExportPipe(MachineIOMode mode) {
        return switch (mode) {
            case NONE -> null;
            case FIRST -> null;
            case SECOND -> null;
            case ALL -> OVERLAY_EXPORT_PIPE_ALL;
            case CE -> null;
        };
    }

    public static void initSprites(TextureAtlas atlas) {
        for (MachineIOMode mode : MachineIOMode.values()) {
            ResourceLocation overlayImport = getOverlayImport(mode);
            if (overlayImport != null) {
                OVERLAY_IMPORT_SPRITES.put(mode, atlas.getSprite(overlayImport));
            }

            ResourceLocation overlayExport = getOverlayExport(mode);
            if (overlayExport != null) {
                OVERLAY_EXPORT_SPRITES.put(mode, atlas.getSprite(overlayExport));
            }
        }
    }

    @Nullable
    public static TextureAtlasSprite getOverlayImportSprite(MachineIOMode mode) {
        return OVERLAY_IMPORT_SPRITES.get(mode);
    }

    @Nullable
    public static TextureAtlasSprite getOverlayExportSprite(MachineIOMode mode) {
        return OVERLAY_EXPORT_SPRITES.get(mode);
    }
}
