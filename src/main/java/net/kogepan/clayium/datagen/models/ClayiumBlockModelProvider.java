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

public class ClayiumBlockModelProvider extends BlockStateProvider {

    private static final ResourceLocation FRONT_OVERLAY_ALL_MODEL = Clayium.id("block/front_overlay_all");

    private static final ResourceLocation[] TIER_BASE_TEXTURES;
    static {
        TIER_BASE_TEXTURES = new ResourceLocation[13];
        for (int i = 1; i <= TIER_BASE_TEXTURES.length; i++) {
            TIER_BASE_TEXTURES[i - 1] = Clayium.id("block/tier/tier" + i);
        }
    }

    private static final ResourceLocation BENDING_MACHINE_TEXTURES = Clayium.id("block/machine/bending_machine");

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

        simpleBlockWithItem(ClayiumBlocks.CLAY_WORK_TABLE.get(), models().cubeColumn(
                ClayiumBlocks.CLAY_WORK_TABLE.getId().getPath(),
                makeId("block/clay_work_table_side"),
                makeId("block/clay_work_table")));

        simpleBlockWithItem(ClayiumBlocks.TEST_CLAY_CONTAINER.get(),
                models().cubeAll(ClayiumBlocks.TEST_CLAY_CONTAINER.getId().getPath(),
                        blockTexture(ClayiumBlocks.TEST_CLAY_CONTAINER.get()))
                        .customLoader(ClayContainerModelBuilder::new).end());

        for (var entry : ClayiumBlocks.BENDING_MACHINE_BLOCKS.int2ObjectEntrySet()) {
            registerSingleMachine(entry.getValue().get(), entry.getIntKey(), BENDING_MACHINE_TEXTURES);
        }
    }

    private void registerSingleMachine(Block block, int tier, ResourceLocation overlay) {
        BlockModelBuilder model = models()
                .withExistingParent(BuiltInRegistries.BLOCK.getKey(block).getPath(), FRONT_OVERLAY_ALL_MODEL)
                .texture("all", TIER_BASE_TEXTURES[tier - 1])
                .texture("overlay_front", overlay)
                .customLoader(ClayContainerModelBuilder::new).end();
        this.horizontalBlock(block, model);
        this.simpleBlockItem(block, model);
    }
}
