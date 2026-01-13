package net.kogepan.clayium.datagen.tags;

import net.kogepan.clayium.Clayium;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ClayiumItemTagsProvider extends ItemTagsProvider {

    public ClayiumItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                   CompletableFuture<TagLookup<Block>> blockTags,
                                   @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Clayium.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {}
}
