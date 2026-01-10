package net.kogepan.clayium.datagen;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ClayiumBlockStateProvider extends BlockStateProvider {

    public ClayiumBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Clayium.MODID, exFileHelper);
    }

    private void simpleBlockAndItem(Block block) {
        ModelFile model = cubeAll(block);
        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockAndItem(ClayiumBlocks.CLAY_ORE.get());
    }
}
