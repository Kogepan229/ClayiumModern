package net.kogepan.clayium.datagen;

import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumDataMaps;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ClayiumDataMapProvider extends DataMapProvider {

    public ClayiumDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(@NotNull HolderLookup.Provider provider) {
        var builder = builder(ClayiumDataMaps.CLAY_ENERGY);

        // Compressed Clay Tier 4+
        // Tier 4: 1 CE, 5: 10 CE, 6: 100 CE, ...
        for (int tier = 4; tier <= 12; tier++) {
            var block = ClayiumBlocks.COMPRESSED_CLAYS.get(tier);
            if (block != null) {
                long energy = (long) Math.pow(10, tier - 4);
                builder.add(block.asItem().builtInRegistryHolder().getDelegate(), CEUtils.ceToLong(energy), false);
            }
        }
    }
}
