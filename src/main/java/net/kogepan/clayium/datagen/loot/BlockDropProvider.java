package net.kogepan.clayium.datagen.loot;

import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredHolder;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public class BlockDropProvider extends BlockLootSubProvider {

    protected BlockDropProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    @NotNull
    protected Iterable<Block> getKnownBlocks() {
        return ClayiumBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get).collect(Collectors.toList());
    }

    @Override
    protected void generate() {
        for (var block : ClayiumBlocks.BLOCKS.getEntries()) {
            add(block.get(), defaultBuilder(block.get()));
        }
    }

    private LootTable.Builder defaultBuilder(Block block) {
        LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(block);
        LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry)
                .when(ExplosionCondition.survivesExplosion());

        return LootTable.lootTable().withPool(pool);
    }
}
