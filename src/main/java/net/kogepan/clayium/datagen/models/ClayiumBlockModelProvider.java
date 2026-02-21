package net.kogepan.clayium.datagen.models;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.registries.ClayiumBlocks;

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

public class ClayiumBlockModelProvider extends BlockStateProvider {

    private static final ResourceLocation OVERLAY_MODEL = Clayium.id("block/overlay");
    private static final ResourceLocation INPUT_ALL_OVERLAY_TEXTURE = Clayium.id("block/overlay/import_all");

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
    private static final ResourceLocation WIRE_DRAWING_MACHINE_TEXTURE = Clayium
            .id("block/machine/wire_drawing_machine");
    private static final ResourceLocation PIPE_DRAWING_MACHINE_TEXTURE = Clayium
            .id("block/machine/pipe_drawing_machine");
    private static final ResourceLocation CUTTING_MACHINE_TEXTURE = Clayium.id("block/machine/cutting_machine");
    private static final ResourceLocation LATHE_TEXTURE = Clayium.id("block/machine/lathe");
    private static final ResourceLocation MILLING_MACHINE_TEXTURE = Clayium.id("block/machine/milling_machine");
    private static final ResourceLocation CLAY_CONDENSER_TEXTURE = Clayium.id("block/machine/clay_condenser");
    private static final ResourceLocation AUTO_CLAY_CONDENSER_TEXTURE = Clayium.id("block/machine/auto_clay_condenser");
    private static final ResourceLocation GRINDER_TEXTURE = Clayium.id("block/machine/grinder");
    private static final ResourceLocation CENTRIFUGE_TEXTURE = Clayium.id("block/machine/centrifuge");
    private static final ResourceLocation DECOMPOSER_TEXTURE = Clayium.id("block/machine/decomposer");
    private static final ResourceLocation INSCRIBER_TEXTURE = Clayium.id("block/machine/inscriber");
    private static final ResourceLocation SMELTER_TEXTURE = Clayium.id("block/machine/smelter");
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

        for (var entry : ClayiumBlocks.CLAY_CONDENSER_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), CLAY_CONDENSER_TEXTURE);
        }
        for (var entry : ClayiumBlocks.AUTO_CLAY_CONDENSER_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), AUTO_CLAY_CONDENSER_TEXTURE);
        }
        for (var entry : ClayiumBlocks.GRINDER_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), GRINDER_TEXTURE);
        }
        for (var entry : ClayiumBlocks.CENTRIFUGE_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), CENTRIFUGE_TEXTURE);
        }
        for (var entry : ClayiumBlocks.DECOMPOSER_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), DECOMPOSER_TEXTURE);
        }
        for (var entry : ClayiumBlocks.INSCRIBER_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), INSCRIBER_TEXTURE);
        }
        for (var entry : ClayiumBlocks.SMELTER_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), SMELTER_TEXTURE);
        }

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
    }

    private void registerSingleMachine(Block block, int tier, @Nullable ResourceLocation overlay) {
        registerSingleMachine(block, tier, overlay, false, false);
    }

    private void registerSingleMachine(Block block, int tier, @Nullable ResourceLocation overlay,
                                       boolean rotateVertical, boolean overlayItemOnly) {
        ClayContainerModelBuilder builder = models().getBuilder(BuiltInRegistries.BLOCK.getKey(block).getPath())
                .customLoader(ClayContainerModelBuilder::new)
                .baseModel(models().nested().parent(models().getExistingFile(models().mcLoc("block/cube_all")))
                        .texture("all", TIER_BASE_TEXTURES[tier - 1]));

        if (overlay != null) {
            builder.overlayModel(
                    models().nested().parent(models().getExistingFile(OVERLAY_MODEL)).texture("overlay_front",
                            overlay));
            if (overlayItemOnly) {
                builder.overlayItemOnly(true);
            }
        }

        if (rotateVertical) {
            builder.rotateVertical(true);
        }

        BlockModelBuilder model = builder.end();

        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }
}
