package net.kogepan.clayium.datagen.loot;

import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class ClayOreLootTableSubProvider implements LootTableSubProvider {

    private final HolderLookup.Provider lookupProvider;

    public ClayOreLootTableSubProvider(HolderLookup.Provider lookupProvider) {
        this.lookupProvider = lookupProvider;
    }

    private Holder<Enchantment> getFortune() {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.lookupProvider
                .lookupOrThrow(Registries.ENCHANTMENT);
        return registrylookup.getOrThrow(Enchantments.FORTUNE);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        consumer.accept(ClayiumBlocks.CLAY_ORE.get().getLootTable(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.CLAY_BALL)
                                .apply(SetItemCountFunction.setCount(
                                        UniformGenerator.between(4.0f, 16.0f)))
                                .apply(ApplyBonusCount.addOreBonusCount(getFortune())))));
    }
}
