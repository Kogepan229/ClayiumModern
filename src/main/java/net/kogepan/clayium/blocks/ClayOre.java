package net.kogepan.clayium.blocks;

import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClayOre extends Block {

    public ClayOre() {
        super(BlockBehaviour.Properties.of()
                .destroyTime(3.0f)
                .explosionResistance(5.0f)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops());
    }

    @Override
    @NotNull
    protected List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootParams.Builder builder) {
        if (!state.is(ClayiumBlocks.CLAY_ORE)) {
            return super.getDrops(state, builder);
        }

        ItemStack tool = builder.getOptionalParameter(LootContextParams.TOOL);
        if (tool == null) {
            return super.getDrops(state, builder);
        }

        HolderLookup.RegistryLookup<Enchantment> registry = builder.getLevel().registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT);

        Holder<Enchantment> silkTouch = registry.getOrThrow(Enchantments.SILK_TOUCH);
        if (EnchantmentHelper.getTagEnchantmentLevel(silkTouch, tool) > 0) {
            return List.of(new ItemStack(ClayiumBlocks.CLAY_ORE, 1));
        }

        Holder<Enchantment> fortune = registry.getOrThrow(Enchantments.FORTUNE);
        int fortuneLevel = EnchantmentHelper.getTagEnchantmentLevel(
                fortune, tool);

        if (tool.is(ClayiumItems.CLAY_PICKAXE) || tool.is(ClayiumItems.CLAY_STEEL_PICKAXE)) {
            fortuneLevel = (fortuneLevel + 1) * 4;
        }

        RandomSource random = builder.getLevel().getRandom();

        int base = 4 + random.nextInt(5) * random.nextInt(4);
        int coefficient = random.nextInt(fortuneLevel + 2) - 1;
        if (coefficient < 0) {
            coefficient = 0;
        }

        int count = base * (coefficient + 1);

        return List.of(new ItemStack(Items.CLAY_BALL, count));
    }
}
