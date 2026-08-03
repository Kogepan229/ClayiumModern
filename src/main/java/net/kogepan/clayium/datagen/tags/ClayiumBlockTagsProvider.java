package net.kogepan.clayium.datagen.tags;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ClayiumBlockTagsProvider extends BlockTagsProvider {

    public ClayiumBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                    @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Clayium.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        Block[] clayOres = {
                ClayiumBlocks.CLAY_ORE.get(),
                ClayiumBlocks.DENSE_CLAY_ORE.get(),
                ClayiumBlocks.LARGE_DENSE_CLAY_ORE.get()
        };
        tag(ClayiumTags.CLAY_ORES).add(clayOres);
        tag(ClayiumTags.CLAY_SHOVEL_MINEABLE_ORES).add(clayOres);

        var clayShovelFastBlocks = tag(ClayiumTags.CLAY_SHOVEL_FAST_BLOCKS)
                .add(Blocks.CLAY)
                .add(ClayiumBlocks.CLAY_CRAFTING_BOARD.get())
                .add(ClayiumBlocks.RAW_CLAY_MACHINE_HULL.get());
        for (int tier = 0; tier <= 12; tier++) {
            clayShovelFastBlocks.add(ClayiumBlocks.COMPRESSED_CLAYS.get(tier).get());
        }

        tag(BlockTags.INCORRECT_FOR_WOODEN_TOOL).add(clayOres);

        tag(BlockTags.LOGS).add(ClayiumBlocks.CLAY_LOG.get());
        tag(BlockTags.LEAVES).add(ClayiumBlocks.CLAY_LEAVES.get());

        addEffectiveTools();
    }

    private void addEffectiveTools() {
        for (var block : ClayiumBlocks.BLOCKS.getEntries()) {

            if (ClayiumBlocks.COMPRESSED_CLAYS.containsValue(block)) {
                tag(BlockTags.MINEABLE_WITH_SHOVEL).add(block.get());
                continue;
            }
            if (block.get() == ClayiumBlocks.CLAY_CRAFTING_BOARD.get() ||
                    block.get() == ClayiumBlocks.RAW_CLAY_MACHINE_HULL.get()) {
                tag(BlockTags.MINEABLE_WITH_SHOVEL).add(block.get());
                continue;
            }
            if (block.get() == ClayiumBlocks.CLAY_LOG.get()) {
                tag(BlockTags.MINEABLE_WITH_AXE).add(block.get());
                continue;
            }
            if (block.get() == ClayiumBlocks.CLAY_LEAVES.get()) {
                tag(BlockTags.MINEABLE_WITH_HOE).add(block.get());
                continue;
            }

            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.get());
        }
    }
}
