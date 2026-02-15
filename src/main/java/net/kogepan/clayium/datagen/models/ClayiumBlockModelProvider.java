package net.kogepan.clayium.datagen.models;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import org.jetbrains.annotations.Nullable;

public class ClayiumBlockModelProvider extends BlockStateProvider {

    private static final ResourceLocation OVERLAY_MODEL = Clayium.id("block/overlay");

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

        for (var entry : ClayiumBlocks.CLAY_BUFFERS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), null);
        }

        for (var entry : ClayiumBlocks.BENDING_MACHINE_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), BENDING_MACHINE_TEXTURE);
        }

        for (var entry : ClayiumBlocks.WATERWHEELS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), WATERWHEEL_TEXTURE, true);
        }

        for (var entry : ClayiumBlocks.COBBLESTONE_GENERATORS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), COBBLESTONE_GENERATOR_TEXTURE, true);
        }
    }

    private void registerSingleMachine(Block block, int tier, @Nullable ResourceLocation overlay) {
        registerSingleMachine(block, tier, overlay, false);
    }

    private void registerSingleMachine(Block block, int tier, @Nullable ResourceLocation overlay,
                                       boolean rotateVertical) {
        ClayContainerModelBuilder builder = models().getBuilder(BuiltInRegistries.BLOCK.getKey(block).getPath())
                .customLoader(ClayContainerModelBuilder::new)
                .baseModel(models().nested().parent(models().getExistingFile(models().mcLoc("block/cube_all")))
                        .texture("all", TIER_BASE_TEXTURES[tier - 1]));

        if (overlay != null) {
            builder.overlayModel(
                    models().nested().parent(models().getExistingFile(OVERLAY_MODEL)).texture("overlay_front",
                            overlay));
        }

        if (rotateVertical) {
            builder.rotateVertical(true);
        }

        BlockModelBuilder model = builder.end();

        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }
}
