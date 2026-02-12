package net.kogepan.clayium.datagen.tags;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
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
        tag(ClayiumTags.CLAY_TOOLS_BONUS_BLOCK)
                .add(Blocks.CLAY)
                .add(ClayiumBlocks.CLAY_ORE.get())
                .add(ClayiumBlocks.DENSE_CLAY_ORE.get())
                .add(ClayiumBlocks.LARGE_DENSE_CLAY_ORE.get());

        addEffectiveTools();
    }

    private void addEffectiveTools() {
        for (var block : ClayiumBlocks.BLOCKS.getEntries()) {

            if (ClayiumBlocks.COMPRESSED_CLAYS.containsValue(block)) {
                tag(BlockTags.MINEABLE_WITH_SHOVEL).add(block.get());
                continue;
            }

            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.get());
        }
    }
}
