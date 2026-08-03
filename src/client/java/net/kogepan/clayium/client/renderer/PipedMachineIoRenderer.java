package net.kogepan.clayium.client.renderer;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.api.configuration.IConfigurationTool;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.platform.Transparency;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quadrant;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class PipedMachineIoRenderer<T extends ClayContainerBlockEntity>
                                         implements BlockEntityRenderer<T, PipedMachineIoRenderState> {

    private static final Direction[] DIRECTIONS = Direction.values();
    private static final MachineIOMode[] IO_MODES = MachineIOMode.values();
    private static final int[] NO_TINTS = new int[0];
    private static final int MODE_BITS = Integer.SIZE / 6;
    private static final int MODE_MASK = (1 << MODE_BITS) - 1;

    private final Map<MachineIOMode, Map<Direction, List<BlockStateModelPart>>> importOverlays;
    private final Map<MachineIOMode, Map<Direction, List<BlockStateModelPart>>> exportOverlays;

    private PipedMachineIoRenderer(OverlayParts overlays) {
        this.importOverlays = overlays.importOverlays;
        this.exportOverlays = overlays.exportOverlays;
    }

    @Override
    public PipedMachineIoRenderState createRenderState() {
        return new PipedMachineIoRenderState();
    }

    @Override
    public void extractRenderState(
                                   T blockEntity,
                                   PipedMachineIoRenderState state,
                                   float partialTicks,
                                   Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        BlockState blockState = blockEntity.getBlockState();
        state.active = blockState.getValue(ClayContainerBlock.PIPE) && isMachineIoOverlayToolHeld();
        if (!state.active) {
            return;
        }

        int connectionMask = 0;
        int packedInputModes = 0;
        int packedOutputModes = 0;
        for (Direction side : DIRECTIONS) {
            int index = side.get3DDataValue();
            if (blockState.getValue(ClayContainerBlock.getConnectionProperty(side))) {
                connectionMask |= 1 << index;
            }
            packedInputModes |= blockEntity.getInputMode(side).ordinal() << modeShift(index);
            packedOutputModes |= blockEntity.getOutputMode(side).ordinal() << modeShift(index);
        }
        state.connectionMask = connectionMask;
        state.packedInputModes = packedInputModes;
        state.packedOutputModes = packedOutputModes;
    }

    @Override
    public void submit(
                       PipedMachineIoRenderState state,
                       PoseStack poseStack,
                       SubmitNodeCollector collector,
                       CameraRenderState camera) {
        if (!state.active) {
            return;
        }

        for (Direction side : DIRECTIONS) {
            int index = side.get3DDataValue();
            if ((state.connectionMask & 1 << index) == 0) {
                continue;
            }
            submitOverlay(collector, poseStack, this.exportOverlays.get(unpackMode(state.packedOutputModes, index)),
                    side,
                    state.lightCoords);
            submitOverlay(collector, poseStack, this.importOverlays.get(unpackMode(state.packedInputModes, index)),
                    side,
                    state.lightCoords);
        }
    }

    private static int modeShift(int directionIndex) {
        return directionIndex * MODE_BITS;
    }

    private static MachineIOMode unpackMode(int packedModes, int directionIndex) {
        int ordinal = packedModes >>> modeShift(directionIndex) & MODE_MASK;
        return ordinal < IO_MODES.length ? IO_MODES[ordinal] : MachineIOMode.NONE;
    }

    private static void submitOverlay(
                                      SubmitNodeCollector collector,
                                      PoseStack poseStack,
                                      @Nullable Map<Direction, List<BlockStateModelPart>> overlays,
                                      Direction side,
                                      int lightCoords) {
        if (overlays == null) {
            return;
        }
        collector.submitBlockModel(
                poseStack,
                RenderTypes.cutoutMovingBlock(),
                overlays.get(side),
                NO_TINTS,
                lightCoords,
                OverlayTexture.NO_OVERLAY,
                0);
    }

    private static boolean isMachineIoOverlayToolHeld() {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        IConfigurationTool tool = player.getMainHandItem().getCapability(ClayiumCapabilities.CONFIGURATION_TOOL);
        return tool != null && tool.rendersMachineIOOverlay();
    }

    private static Map<MachineIOMode, Map<Direction, List<BlockStateModelPart>>> bakeOverlays(
                                                                                              SpriteGetter sprites,
                                                                                              boolean input,
                                                                                              float offset) {
        Map<MachineIOMode, Map<Direction, List<BlockStateModelPart>>> overlays = new EnumMap<>(MachineIOMode.class);
        for (MachineIOMode mode : MachineIOMode.values()) {
            @Nullable
            Identifier texture = input ? getImportTexture(mode) : getExportTexture(mode);
            if (texture == null) {
                continue;
            }
            TextureAtlasSprite sprite = sprites.get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, texture));
            Material.Baked material = new Material.Baked(sprite, false);
            BakedQuad.MaterialInfo materialInfo = BakedQuad.MaterialInfo.of(
                    material,
                    Transparency.TRANSPARENT,
                    -1,
                    true,
                    0,
                    true);
            Map<Direction, List<BlockStateModelPart>> bySide = new EnumMap<>(Direction.class);
            for (Direction side : DIRECTIONS) {
                bySide.put(side, List.of(bakeArmOverlay(side, sprite, material, materialInfo, offset)));
            }
            overlays.put(mode, Map.copyOf(bySide));
        }
        return Map.copyOf(overlays);
    }

    private static BlockStateModelPart bakeArmOverlay(
                                                      Direction armDirection,
                                                      TextureAtlasSprite sprite,
                                                      Material.Baked material,
                                                      BakedQuad.MaterialInfo materialInfo,
                                                      float offset) {
        Vector3f[] bounds = armBounds(armDirection);
        Vector3f from = bounds[0].sub(offset, offset, offset);
        Vector3f to = bounds[1].add(offset, offset, offset);
        List<BakedQuad> quads = new java.util.ArrayList<>(5);
        for (Direction face : DIRECTIONS) {
            if (face != armDirection.getOpposite()) {
                quads.add(bakeQuad(from, to, armDirection, face, sprite, materialInfo));
            }
        }
        return new PipeOverlayPart(List.copyOf(quads), material, materialInfo.flags());
    }

    private static BakedQuad bakeQuad(
                                      Vector3f from,
                                      Vector3f to,
                                      Direction armDirection,
                                      Direction face,
                                      TextureAtlasSprite sprite,
                                      BakedQuad.MaterialInfo materialInfo) {
        FaceInfo faceInfo = FaceInfo.fromFacing(face);
        CuboidFace.UVs uvs = armUv(armDirection, face);
        Vector3f[] positions = new Vector3f[4];
        long[] packedUvs = new long[4];
        for (int vertex = 0; vertex < 4; vertex++) {
            positions[vertex] = faceInfo.getVertexInfo(vertex).select(from, to).div(16.0F);
            packedUvs[vertex] = UVPair.pack(
                    sprite.getU(CuboidFace.getU(uvs, Quadrant.R0, vertex)),
                    sprite.getV(CuboidFace.getV(uvs, Quadrant.R0, vertex)));
        }
        return new BakedQuad(
                positions[0],
                positions[1],
                positions[2],
                positions[3],
                packedUvs[0],
                packedUvs[1],
                packedUvs[2],
                packedUvs[3],
                face,
                materialInfo);
    }

    private static CuboidFace.UVs armUv(Direction armDirection, Direction face) {
        if (armDirection == face) {
            return new CuboidFace.UVs(5, 5, 11, 11);
        }
        return switch (armDirection) {
            case DOWN -> new CuboidFace.UVs(5, 11, 11, 16);
            case UP -> new CuboidFace.UVs(5, 0, 11, 5);
            case NORTH -> switch (face) {
                case UP -> new CuboidFace.UVs(5, 0, 11, 5);
                case DOWN -> new CuboidFace.UVs(5, 11, 11, 16);
                case WEST -> new CuboidFace.UVs(0, 5, 5, 11);
                case EAST -> new CuboidFace.UVs(11, 5, 16, 11);
                default -> throw new IllegalArgumentException("Invalid north arm face: " + face);
            };
            case SOUTH -> switch (face) {
                case UP -> new CuboidFace.UVs(5, 11, 11, 16);
                case DOWN -> new CuboidFace.UVs(5, 0, 11, 5);
                case WEST -> new CuboidFace.UVs(11, 5, 16, 11);
                case EAST -> new CuboidFace.UVs(0, 5, 5, 11);
                default -> throw new IllegalArgumentException("Invalid south arm face: " + face);
            };
            case WEST -> switch (face) {
                case UP, DOWN -> new CuboidFace.UVs(0, 5, 5, 11);
                case NORTH -> new CuboidFace.UVs(11, 5, 16, 11);
                case SOUTH -> new CuboidFace.UVs(0, 5, 5, 11);
                default -> throw new IllegalArgumentException("Invalid west arm face: " + face);
            };
            case EAST -> switch (face) {
                case UP, DOWN -> new CuboidFace.UVs(11, 5, 16, 11);
                case NORTH -> new CuboidFace.UVs(0, 5, 5, 11);
                case SOUTH -> new CuboidFace.UVs(11, 5, 16, 11);
                default -> throw new IllegalArgumentException("Invalid east arm face: " + face);
            };
        };
    }

    private static Vector3f[] armBounds(Direction side) {
        return switch (side) {
            case DOWN -> new Vector3f[] { new Vector3f(5, 0, 5), new Vector3f(11, 5, 11) };
            case UP -> new Vector3f[] { new Vector3f(5, 11, 5), new Vector3f(11, 16, 11) };
            case NORTH -> new Vector3f[] { new Vector3f(5, 5, 0), new Vector3f(11, 11, 5) };
            case SOUTH -> new Vector3f[] { new Vector3f(5, 5, 11), new Vector3f(11, 11, 16) };
            case WEST -> new Vector3f[] { new Vector3f(0, 5, 5), new Vector3f(5, 11, 11) };
            case EAST -> new Vector3f[] { new Vector3f(11, 5, 5), new Vector3f(16, 11, 11) };
        };
    }

    private static @Nullable Identifier getImportTexture(MachineIOMode mode) {
        return switch (mode) {
            case NONE -> null;
            case FIRST -> Clayium.id("block/overlay/import_pipe_1");
            case SECOND -> Clayium.id("block/overlay/import_pipe_2");
            case ALL -> Clayium.id("block/overlay/import_pipe_all");
            case CE -> Clayium.id("block/overlay/import_pipe_ce");
            case M_ALL -> Clayium.id("block/overlay/import_m_all_p");
            case M_1 -> Clayium.id("block/overlay/import_m1_p");
            case M_2 -> Clayium.id("block/overlay/import_m2_p");
            case M_3 -> Clayium.id("block/overlay/import_m3_p");
            case M_4 -> Clayium.id("block/overlay/import_m4_p");
            case M_5 -> Clayium.id("block/overlay/import_m5_p");
            case M_6 -> Clayium.id("block/overlay/import_m6_p");
        };
    }

    private static @Nullable Identifier getExportTexture(MachineIOMode mode) {
        return switch (mode) {
            case NONE, CE -> null;
            case FIRST -> Clayium.id("block/overlay/export_pipe_1");
            case SECOND -> Clayium.id("block/overlay/export_pipe_2");
            case ALL -> Clayium.id("block/overlay/export_pipe_all");
            case M_ALL -> Clayium.id("block/overlay/export_m_all_p");
            case M_1 -> Clayium.id("block/overlay/export_m1_p");
            case M_2 -> Clayium.id("block/overlay/export_m2_p");
            case M_3 -> Clayium.id("block/overlay/export_m3_p");
            case M_4 -> Clayium.id("block/overlay/export_m4_p");
            case M_5 -> Clayium.id("block/overlay/export_m5_p");
            case M_6 -> Clayium.id("block/overlay/export_m6_p");
        };
    }

    public static final class Factory {

        private BlockEntityRendererProvider.@Nullable Context context;
        private @Nullable OverlayParts overlays;

        public synchronized <T extends ClayContainerBlockEntity> PipedMachineIoRenderer<T> create(
                                                                                                  BlockEntityRendererProvider.Context context) {
            OverlayParts current = this.overlays;
            if (this.context != context || current == null) {
                current = new OverlayParts(
                        bakeOverlays(context.sprites(), true, 0.02F),
                        bakeOverlays(context.sprites(), false, 0.01F));
                this.context = context;
                this.overlays = current;
            }
            return new PipedMachineIoRenderer<>(current);
        }
    }

    private record OverlayParts(
                                Map<MachineIOMode, Map<Direction, List<BlockStateModelPart>>> importOverlays,
                                Map<MachineIOMode, Map<Direction, List<BlockStateModelPart>>> exportOverlays) {}

    private record PipeOverlayPart(
                                   List<BakedQuad> quads,
                                   Material.Baked particleMaterial,
                                   int materialFlags)
            implements BlockStateModelPart {

        @Override
        public List<BakedQuad> getQuads(@Nullable Direction direction) {
            return direction == null ? this.quads : List.of();
        }

        @Override
        @SuppressWarnings("deprecation")
        public boolean useAmbientOcclusion() {
            return true;
        }
    }
}
