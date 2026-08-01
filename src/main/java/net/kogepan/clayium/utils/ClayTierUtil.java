package net.kogepan.clayium.utils;

import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;

/**
 * Clay compression tier helpers shared by machines that treat vanilla clay as tier 0 and
 * {@link ClayiumBlocks#COMPRESSED_CLAYS} block {@code n} as tier {@code n + 1}.
 */
public final class ClayTierUtil {

    private ClayTierUtil() {}

    /**
     * Returns the clay tier for a stack, or -1 if the item is not vanilla clay or a compressed clay block.
     */
    public static int getClayTier(@NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return -1;
        }

        Block block = Block.byItem(stack.getItem());
        if (block == Blocks.CLAY) {
            return 0;
        }

        for (var entry : ClayiumBlocks.COMPRESSED_CLAYS.int2ObjectEntrySet()) {
            if (entry.getValue().get().asItem() == stack.getItem()) {
                return entry.getIntKey() + 1;
            }
        }

        return -1;
    }

    /**
     * Creates a stack of clay or compressed clay for the given tier and count.
     *
     * @param tier   0 = vanilla clay, {@code k} &gt;= 1 = compressed clay map key {@code k - 1}
     * @param amount stack size
     */
    @NotNull
    public static ItemStack createClayStack(int tier, int amount) {
        if (tier == 0) {
            return new ItemStack(Blocks.CLAY, amount);
        }
        Block block = ClayiumBlocks.COMPRESSED_CLAYS.get(tier - 1).get();
        return new ItemStack(block.asItem(), amount);
    }
}
