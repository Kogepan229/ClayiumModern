package net.kogepan.clayium.datagen.models;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blockentities.machine.ClayBlastFurnaceBlockEntity;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class ClayiumBlockModelProvider extends BlockStateProvider {

    private static final ResourceLocation OVERLAY_ALL_CUBE_MODEL = Clayium.id("block/overlay_all_cube");
    private static final ResourceLocation OVERLAY_MODEL = Clayium.id("block/overlay");
    private static final ResourceLocation OVERLAY_TOP_MODEL = Clayium.id("block/overlay_top");
    private static final ResourceLocation OVERLAY_ALL_MODEL = Clayium.id("block/overlay_all");
    private static final ResourceLocation INPUT_ALL_OVERLAY_TEXTURE = Clayium.id("block/overlay/import_all");
    private static final ResourceLocation AZ91D_ALLOY_HULL_TEXTURE = Clayium.id("block/az91d_alloy_hull");

    private static final ResourceLocation STORAGE_CONTAINER_FRONT_TEXTURE = Clayium
            .id("block/machine/storage_container_front");
    private static final ResourceLocation STORAGE_CONTAINER_SIDE_TEXTURE = Clayium
            .id("block/machine/storage_container_side");
    private static final ResourceLocation STORAGE_CONTAINER_TOP_TEXTURE = Clayium
            .id("block/machine/storage_container_top");
    private static final ResourceLocation STORAGE_CONTAINER_UPGRADED_TEXTURE = Clayium
            .id("block/machine/storage_container_upgraded");
    private static final ResourceLocation VACUUM_CONTAINER_FRONT_TEXTURE = Clayium
            .id("block/machine/vacuum_container_front");
    private static final ResourceLocation VACUUM_CONTAINER_SIDE_TEXTURE = Clayium
            .id("block/machine/vacuum_container_side");
    private static final ResourceLocation VACUUM_CONTAINER_TOP_TEXTURE = Clayium
            .id("block/machine/vacuum_container_top");

    private static final ResourceLocation[] TIER_BASE_TEXTURES;
    static {
        TIER_BASE_TEXTURES = new ResourceLocation[13];
        for (int i = 1; i <= TIER_BASE_TEXTURES.length; i++) {
            TIER_BASE_TEXTURES[i - 1] = Clayium.id("block/tier/tier" + i);
        }
    }

    private static final ResourceLocation BENDING_MACHINE_TEXTURE = Clayium.id("block/machine/bending_machine");
    private static final ResourceLocation WATERWHEEL_TEXTURE = Clayium.id("block/machine/waterwheel");
    private static final ResourceLocation COBBLESTONE_GENERATOR_TEXTURE = Clayium
            .id("block/machine/cobblestone_generator");
    private static final ResourceLocation SALT_EXTRACTOR_TEXTURE = Clayium.id("block/machine/salt_extractor");
    private static final ResourceLocation WIRE_DRAWING_MACHINE_TEXTURE = Clayium
            .id("block/machine/wire_drawing_machine");
    private static final ResourceLocation PIPE_DRAWING_MACHINE_TEXTURE = Clayium
            .id("block/machine/pipe_drawing_machine");
    private static final ResourceLocation CUTTING_MACHINE_TEXTURE = Clayium.id("block/machine/cutting_machine");
    private static final ResourceLocation LATHE_TEXTURE = Clayium.id("block/machine/lathe");
    private static final ResourceLocation MILLING_MACHINE_TEXTURE = Clayium.id("block/machine/milling_machine");
    private static final ResourceLocation CLAY_CONDENSER_TEXTURE = Clayium.id("block/machine/clay_condenser");
    private static final ResourceLocation AUTO_CLAY_CONDENSER_TEXTURE = Clayium.id("block/machine/auto_clay_condenser");
    private static final ResourceLocation SOLAR_CLAY_FABRICATOR_TEXTURE = Clayium.id("block/machine/solar");
    private static final ResourceLocation CLAY_FABRICATOR_TEXTURE = Clayium.id("block/machine/clay_fabricator");
    private static final ResourceLocation GRINDER_TEXTURE = Clayium.id("block/machine/grinder");
    private static final ResourceLocation CENTRIFUGE_TEXTURE = Clayium.id("block/machine/centrifuge");
    private static final ResourceLocation CHEMICAL_REACTOR_TEXTURE = Clayium.id("block/machine/chemical_reactor");
    private static final ResourceLocation DECOMPOSER_TEXTURE = Clayium.id("block/machine/decomposer");
    private static final ResourceLocation ELECTROLYSIS_REACTOR_TEXTURE = Clayium
            .id("block/machine/electrolysis_reactor");
    private static final ResourceLocation DISTRIBUTOR_TEXTURE = Clayium.id("block/machine/distributor");
    private static final ResourceLocation INSCRIBER_TEXTURE = Clayium.id("block/machine/inscriber");
    private static final ResourceLocation SMELTER_TEXTURE = Clayium.id("block/machine/smelter");
    private static final ResourceLocation CLAY_BLAST_FURNACE_TEXTURE_UNFORMED = Clayium
            .id("block/machine/clay_blast_furnace_unformed");
    private static final ResourceLocation CLAY_BLAST_FURNACE_TEXTURE_FORMED = Clayium
            .id("block/machine/clay_blast_furnace_formed");
    private static final ResourceLocation CHUNK_LOADER_TEXTURE = Clayium.id("block/machine/chunk_loader");
    private static final ResourceLocation CLAY_INTERFACE_TEXTURE = Clayium.id("block/machine/clay_interface");
    private static final ResourceLocation CLAY_LASER_TEXTURE = Clayium.id("block/machine/clay_laser");

    public ClayiumBlockModelProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Clayium.MODID, exFileHelper);
    }

    static ResourceLocation makeId(String id) {
        return ResourceLocation.fromNamespaceAndPath(Clayium.MODID, id);
    }

    private void simpleBlockAndItem(Block block) {
        ModelFile model = cubeAll(block);
        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockAndItem(ClayiumBlocks.CLAY_ORE.get());
        simpleBlockAndItem(ClayiumBlocks.DENSE_CLAY_ORE.get());
        simpleBlockAndItem(ClayiumBlocks.LARGE_DENSE_CLAY_ORE.get());

        simpleBlockWithItem(ClayiumBlocks.CLAY_WORK_TABLE.get(), models().cubeColumn(
                ClayiumBlocks.CLAY_WORK_TABLE.getId().getPath(),
                makeId("block/clay_work_table_side"),
                makeId("block/clay_work_table")));

        // Quartz Crucible: blockstate is in main (multipart: empty + level_1..9). Item uses empty model.
        ModelFile quartzCrucibleItemModel = models().getExistingFile(Clayium.id("block/quartz_crucible/base"));
        simpleBlockItem(ClayiumBlocks.QUARTZ_CRUCIBLE.get(), quartzCrucibleItemModel);

        for (var entry : ClayiumBlocks.COMPRESSED_CLAYS.values()) {
            simpleBlockAndItem(entry.get());
        }

        simpleBlockAndItem(ClayiumBlocks.RAW_CLAY_MACHINE_HULL.get());
        for (var entry : ClayiumBlocks.MACHINE_HULLS.values()) {
            simpleBlockAndItem(entry.get());
        }
        simpleBlockAndItem(ClayiumBlocks.AZ91D_ALLOY_HULL.get());
        simpleBlockAndItem(ClayiumBlocks.ZK60A_ALLOY_HULL.get());

        for (var entry : ClayiumBlocks.CLAY_BUFFERS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), INPUT_ALL_OVERLAY_TEXTURE, false, true);
        }

        for (var entry : ClayiumBlocks.MULTITRACK_BUFFERS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), INPUT_ALL_OVERLAY_TEXTURE, false, true);
        }

        for (var entry : ClayiumBlocks.BENDING_MACHINE_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), BENDING_MACHINE_TEXTURE);
        }
        for (var entry : ClayiumBlocks.WIRE_DRAWING_MACHINE_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), WIRE_DRAWING_MACHINE_TEXTURE);
        }
        for (var entry : ClayiumBlocks.PIPE_DRAWING_MACHINE_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), PIPE_DRAWING_MACHINE_TEXTURE);
        }
        for (var entry : ClayiumBlocks.CUTTING_MACHINE_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), CUTTING_MACHINE_TEXTURE);
        }
        for (var entry : ClayiumBlocks.LATHE_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), LATHE_TEXTURE);
        }
        for (var entry : ClayiumBlocks.MILLING_MACHINE_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), MILLING_MACHINE_TEXTURE);
        }

        for (var entry : ClayiumBlocks.WATERWHEELS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), WATERWHEEL_TEXTURE, true, false);
        }

        for (var entry : ClayiumBlocks.COBBLESTONE_GENERATORS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), COBBLESTONE_GENERATOR_TEXTURE, true,
                    false);
        }

        for (var entry : ClayiumBlocks.SALT_EXTRACTOR_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), SALT_EXTRACTOR_TEXTURE, true, false);
        }

        for (var entry : ClayiumBlocks.CLAY_CONDENSER_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), CLAY_CONDENSER_TEXTURE);
        }
        for (var entry : ClayiumBlocks.AUTO_CLAY_CONDENSER_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), AUTO_CLAY_CONDENSER_TEXTURE);
        }
        for (var entry : ClayiumBlocks.SOLAR_CLAY_FABRICATOR_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), SOLAR_CLAY_FABRICATOR_TEXTURE,
                    OVERLAY_TOP_MODEL, false, false);
        }
        for (var entry : ClayiumBlocks.CLAY_FABRICATOR_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), CLAY_FABRICATOR_TEXTURE);
        }
        for (var entry : ClayiumBlocks.GRINDER_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), GRINDER_TEXTURE);
        }
        for (var entry : ClayiumBlocks.CENTRIFUGE_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), CENTRIFUGE_TEXTURE);
        }
        for (var entry : ClayiumBlocks.CHEMICAL_REACTOR_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), CHEMICAL_REACTOR_TEXTURE);
        }
        for (var entry : ClayiumBlocks.DECOMPOSER_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), DECOMPOSER_TEXTURE);
        }
        for (var entry : ClayiumBlocks.ELECTROLYSIS_REACTOR_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), ELECTROLYSIS_REACTOR_TEXTURE);
        }
        for (var entry : ClayiumBlocks.DISTRIBUTOR_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), DISTRIBUTOR_TEXTURE, OVERLAY_ALL_MODEL,
                    false, false);
        }
        for (var entry : ClayiumBlocks.INSCRIBER_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), INSCRIBER_TEXTURE);
        }
        for (var entry : ClayiumBlocks.SMELTER_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), SMELTER_TEXTURE);
        }
        registerSingleMachine(ClayiumBlocks.CLAY_BLAST_FURNACE.get(), 6, CLAY_BLAST_FURNACE_TEXTURE_UNFORMED,
                Map.of(ClayBlastFurnaceBlockEntity.FRONT_OVERLAY_VARIANT_FORMED, CLAY_BLAST_FURNACE_TEXTURE_FORMED),
                false, false);

        for (var entry : ClayiumBlocks.CLAY_INTERFACE_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), CLAY_INTERFACE_TEXTURE, OVERLAY_ALL_MODEL,
                    false, false);
        }

        registerFilteredContainers();

        for (var entry : ClayiumBlocks.CLAY_LASER_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), CLAY_LASER_TEXTURE, true, false);
        }

        // Laser Reflector: custom pyramid geometry model; block uses ENTITYBLOCK_ANIMATED; item uses BEWLR
        // (builtin/entity)
        ResourceLocation laserReflectorTexture = Clayium.id("block/machine/laser_reflector");
        BlockModelBuilder laserReflectorBuilder = models().getBuilder("laser_reflector");
        laserReflectorBuilder.customLoader(net.kogepan.clayium.datagen.models.LaserReflectorModelBuilder::new)
                .texture(laserReflectorTexture);
        directionalBlock(ClayiumBlocks.LASER_REFLECTOR.get(), laserReflectorBuilder);
        simpleBlockItem(ClayiumBlocks.LASER_REFLECTOR.get(),
                new ModelFile.UncheckedModelFile("minecraft:builtin/entity"));

        // Creative CE Source: texture from ClayiumOriginal (creativeenergy.png)
        ResourceLocation creativeCeSourceTexture = Clayium.id("block/machine/creative_ce_source");
        ModelFile creativeCeSourceModel = models().getBuilder("creative_ce_source")
                .parent(models().getExistingFile(models().mcLoc("block/cube_all")))
                .texture("all", creativeCeSourceTexture);
        simpleBlock(ClayiumBlocks.CREATIVE_CE_SOURCE.get(), creativeCeSourceModel);
        simpleBlockItem(ClayiumBlocks.CREATIVE_CE_SOURCE.get(), creativeCeSourceModel);

        // Clay Sapling: block uses cross model with cutout; blockstate has stage 0 and 1 (same model)
        ResourceLocation claySaplingTexture = Clayium.id("block/clay_sapling");
        ModelFile claySaplingBlockModel = models().getBuilder("clay_sapling")
                .parent(models().getExistingFile(models().mcLoc("block/cross")))
                .texture("cross", claySaplingTexture)
                .renderType("minecraft:cutout");
        VariantBlockStateBuilder claySaplingBuilder = getVariantBuilder(ClayiumBlocks.CLAY_SAPLING.get());
        claySaplingBuilder
                .partialState().with(BlockStateProperties.STAGE, 0)
                .addModels(new ConfiguredModel(claySaplingBlockModel))
                .partialState().with(BlockStateProperties.STAGE, 1)
                .addModels(new ConfiguredModel(claySaplingBlockModel));
        // Item uses flat texture (item/generated)
        itemModels().getBuilder("clay_sapling")
                .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
                .texture("layer0", claySaplingTexture);

        // Clay Log: column (side + end)
        ResourceLocation clayLogSide = Clayium.id("block/clay_log");
        ResourceLocation clayLogTop = Clayium.id("block/clay_log_top");
        ModelFile clayLogModel = models().cubeColumn("clay_log", clayLogSide, clayLogTop);
        axisBlock(ClayiumBlocks.CLAY_LOG.get(), clayLogModel, clayLogModel);
        simpleBlockItem(ClayiumBlocks.CLAY_LOG.get(), clayLogModel);

        // Clay Leaves: cube_all with cutout
        ResourceLocation clayLeavesTexture = Clayium.id("block/clay_leaves");
        ModelFile clayLeavesModel = models().getBuilder("clay_leaves")
                .parent(models().getExistingFile(models().mcLoc("block/leaves")))
                .texture("all", clayLeavesTexture)
                .renderType("minecraft:cutout");
        simpleBlock(ClayiumBlocks.CLAY_LEAVES.get(), clayLeavesModel);
        simpleBlockItem(ClayiumBlocks.CLAY_LEAVES.get(), clayLeavesModel);

        ModelFile chunkLoaderModel = models().getBuilder("chunk_loader")
                .parent(models().getExistingFile(OVERLAY_ALL_CUBE_MODEL))
                .texture("all", Clayium.id("block/zk60a_alloy_hull"))
                .texture("overlay", CHUNK_LOADER_TEXTURE)
                .renderType(RenderType.cutout().name);
        this.simpleBlock(ClayiumBlocks.CHUNK_LOADER.get(), chunkLoaderModel);
        this.simpleBlockItem(ClayiumBlocks.CHUNK_LOADER.get(), chunkLoaderModel);
    }

    private void registerFilteredContainers() {
        Block storageContainer = ClayiumBlocks.STORAGE_CONTAINER.get();
        String storageName = BuiltInRegistries.BLOCK.getKey(storageContainer).getPath();
        BlockModelBuilder storageModel = models().getBuilder(storageName)
                .renderType(RenderType.translucent().name)
                .customLoader(ClayContainerModelBuilder::new)
                .baseModel(models().nested().parent(models().getExistingFile(models().mcLoc("block/cube_all")))
                        .texture("all", AZ91D_ALLOY_HULL_TEXTURE))
                .overlayModel(containerOverlayModel(
                        STORAGE_CONTAINER_FRONT_TEXTURE,
                        STORAGE_CONTAINER_SIDE_TEXTURE,
                        STORAGE_CONTAINER_TOP_TEXTURE,
                        true,
                        null))
                .overlayModelVariant("upgraded", containerOverlayModel(
                        STORAGE_CONTAINER_FRONT_TEXTURE,
                        STORAGE_CONTAINER_SIDE_TEXTURE,
                        STORAGE_CONTAINER_TOP_TEXTURE,
                        true,
                        STORAGE_CONTAINER_UPGRADED_TEXTURE))
                .end();
        simpleBlock(storageContainer, storageModel);

        BlockModelBuilder storageInventoryModel = containerInventoryModel(
                storageName + "_inventory",
                STORAGE_CONTAINER_FRONT_TEXTURE,
                STORAGE_CONTAINER_SIDE_TEXTURE,
                STORAGE_CONTAINER_TOP_TEXTURE,
                true,
                null);
        BlockModelBuilder upgradedStorageInventoryModel = containerInventoryModel(
                storageName + "_upgraded_inventory",
                STORAGE_CONTAINER_FRONT_TEXTURE,
                STORAGE_CONTAINER_SIDE_TEXTURE,
                STORAGE_CONTAINER_TOP_TEXTURE,
                true,
                STORAGE_CONTAINER_UPGRADED_TEXTURE);
        itemModels().getBuilder(storageName)
                .parent(storageInventoryModel)
                .override()
                .predicate(Clayium.id("upgraded"), 1.0F)
                .model(upgradedStorageInventoryModel)
                .end();

        Block vacuumContainer = ClayiumBlocks.VACUUM_CONTAINER.get();
        String vacuumName = BuiltInRegistries.BLOCK.getKey(vacuumContainer).getPath();
        BlockModelBuilder vacuumModel = models().getBuilder(vacuumName)
                .renderType(RenderType.translucent().name)
                .customLoader(ClayContainerModelBuilder::new)
                .baseModel(models().nested().parent(models().getExistingFile(models().mcLoc("block/cube_all")))
                        .texture("all", AZ91D_ALLOY_HULL_TEXTURE))
                .overlayModel(containerOverlayModel(
                        VACUUM_CONTAINER_FRONT_TEXTURE,
                        VACUUM_CONTAINER_SIDE_TEXTURE,
                        VACUUM_CONTAINER_TOP_TEXTURE,
                        false,
                        null))
                .end();
        simpleBlock(vacuumContainer, vacuumModel);

        BlockModelBuilder vacuumInventoryModel = containerInventoryModel(
                vacuumName + "_inventory",
                VACUUM_CONTAINER_FRONT_TEXTURE,
                VACUUM_CONTAINER_SIDE_TEXTURE,
                VACUUM_CONTAINER_TOP_TEXTURE,
                false,
                null);
        itemModels().getBuilder(vacuumName).parent(vacuumInventoryModel);
    }

    private BlockModelBuilder containerOverlayModel(
                                                    ResourceLocation frontTexture,
                                                    ResourceLocation sideTexture,
                                                    ResourceLocation topTexture,
                                                    boolean renderBottom,
                                                    @Nullable ResourceLocation upgradeTexture) {
        BlockModelBuilder model = models().nested()
                .parent(models().getExistingFile(models().mcLoc("block/block")))
                .texture("front", frontTexture)
                .texture("side", sideTexture)
                .texture("top", topTexture);
        addContainerOverlayElement(model, -0.01F, 16.01F, renderBottom);

        if (upgradeTexture != null) {
            model.texture("upgrade", upgradeTexture);
            model.element()
                    .from(-0.02F, -0.02F, -0.02F)
                    .to(16.02F, 16.02F, 16.02F)
                    .allFaces((direction, face) -> face
                            .uvs(0, 0, 16, 16)
                            .texture("#upgrade")
                            .cullface(direction))
                    .end();
        }
        return model;
    }

    private BlockModelBuilder containerInventoryModel(
                                                      String modelName,
                                                      ResourceLocation frontTexture,
                                                      ResourceLocation sideTexture,
                                                      ResourceLocation topTexture,
                                                      boolean renderBottom,
                                                      @Nullable ResourceLocation upgradeTexture) {
        BlockModelBuilder model = models().getBuilder(modelName)
                .parent(models().getExistingFile(models().mcLoc("block/block")))
                .texture("particle", AZ91D_ALLOY_HULL_TEXTURE)
                .texture("base", AZ91D_ALLOY_HULL_TEXTURE)
                .texture("front", frontTexture)
                .texture("side", sideTexture)
                .texture("top", topTexture)
                .renderType(RenderType.translucent().name);
        model.element().cube("#base").end();
        addContainerOverlayElement(model, -0.01F, 16.01F, renderBottom);

        if (upgradeTexture != null) {
            model.texture("upgrade", upgradeTexture);
            model.element()
                    .from(-0.02F, -0.02F, -0.02F)
                    .to(16.02F, 16.02F, 16.02F)
                    .allFaces((direction, face) -> face
                            .uvs(0, 0, 16, 16)
                            .texture("#upgrade")
                            .cullface(direction))
                    .end();
        }
        return model;
    }

    private static void addContainerOverlayElement(
                                                   BlockModelBuilder model, float from, float to,
                                                   boolean renderBottom) {
        model.element()
                .from(from, from, from)
                .to(to, to, to)
                .allFacesExcept((direction, face) -> face
                        .uvs(0, 0, 16, 16)
                        .texture(switch (direction) {
                            case UP, DOWN -> "#top";
                            case NORTH -> "#front";
                            case SOUTH, WEST, EAST -> "#side";
                        })
                        .cullface(direction),
                        renderBottom ? Set.of() : Set.of(Direction.DOWN))
                .end();
    }

    private void registerSingleMachine(Block block, int tier, @Nullable ResourceLocation overlay) {
        registerSingleMachine(block, tier, overlay, false, false);
    }

    private void registerSingleMachine(Block block, int tier, @Nullable ResourceLocation overlay,
                                       boolean rotateVertical, boolean overlayItemOnly) {
        registerSingleMachine(block, tier, overlay, Map.of(), OVERLAY_MODEL, rotateVertical, overlayItemOnly);
    }

    private void registerSingleMachine(Block block, int tier, @Nullable ResourceLocation overlay,
                                       Map<String, ResourceLocation> overlayVariants,
                                       boolean rotateVertical, boolean overlayItemOnly) {
        registerSingleMachine(block, tier, overlay, overlayVariants, OVERLAY_MODEL, rotateVertical, overlayItemOnly);
    }

    private void registerSingleMachine(Block block, int tier, @Nullable ResourceLocation overlay,
                                       ResourceLocation overlayModel, boolean rotateVertical, boolean overlayItemOnly) {
        registerSingleMachine(block, tier, overlay, Map.of(), overlayModel, rotateVertical, overlayItemOnly);
    }

    private void registerSingleMachine(Block block, int tier, @Nullable ResourceLocation overlay,
                                       Map<String, ResourceLocation> overlayVariants,
                                       ResourceLocation overlayModel, boolean rotateVertical, boolean overlayItemOnly) {
        ClayContainerModelBuilder builder = models().getBuilder(BuiltInRegistries.BLOCK.getKey(block).getPath())
                .customLoader(ClayContainerModelBuilder::new)
                .baseModel(models().nested().parent(models().getExistingFile(models().mcLoc("block/cube_all")))
                        .texture("all", TIER_BASE_TEXTURES[tier - 1]));

        if (overlay != null) {
            builder.overlayModel(
                    models().nested().parent(models().getExistingFile(overlayModel)).texture("overlay_front",
                            overlay));
            if (overlayItemOnly) {
                builder.overlayItemOnly(true);
            }
        }
        for (var entry : overlayVariants.entrySet()) {
            builder.overlayModelVariant(entry.getKey(),
                    models().nested().parent(models().getExistingFile(overlayModel)).texture("overlay_front",
                            entry.getValue()));
        }

        if (rotateVertical) {
            builder.rotateVertical(true);
        }

        BlockModelBuilder model = builder.end();

        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }
}
