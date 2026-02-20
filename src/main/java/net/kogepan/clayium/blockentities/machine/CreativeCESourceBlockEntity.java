package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

import org.jetbrains.annotations.NotNull;

/**
 * Block entity for Creative CE Source. Exposes an item handler that provides infinite
 * highest-tier energized clay (compressed_clay_12 - Octuple Compressed Energetic Clay).
 * <p>
 * Based on ClayiumOriginal's TileCreativeEnergySource and ClayiumUnofficial's
 * TileEntityCreativeEnergySource.
 */
public class CreativeCESourceBlockEntity extends BlockEntity {

    private static final int HIGHEST_COMPRESSED_CLAY_TIER = 12;

    private final IItemHandler infiniteHandler = new InfiniteEnergizedClayHandler();

    public CreativeCESourceBlockEntity(BlockPos pos, BlockState blockState) {
        super(ClayiumBlockEntityTypes.CREATIVE_CE_SOURCE_BLOCK_ENTITY.get(), pos, blockState);
    }

    /**
     * Returns the item handler that provides infinite highest-tier energized clay.
     * Exposed via Capabilities.ItemHandler.BLOCK.
     */
    public IItemHandler getItemHandler(@SuppressWarnings("unused") Direction side) {
        return infiniteHandler;
    }

    /**
     * Item handler that always "contains" a full stack of the highest-tier compressed clay
     * and allows unlimited extraction (take-only). Insert is rejected.
     */
    private static final class InfiniteEnergizedClayHandler implements IItemHandler {

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        @NotNull
        public ItemStack getStackInSlot(int slot) {
            if (slot != 0) return ItemStack.EMPTY;
            return getTemplateStack();
        }

        @Override
        @NotNull
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        @NotNull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != 0 || amount <= 0) return ItemStack.EMPTY;
            ItemStack template = getTemplateStack();
            int toGive = Math.min(amount, template.getMaxStackSize());
            if (toGive <= 0) return ItemStack.EMPTY;
            return new ItemStack(template.getItem(), toGive);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return false;
        }

        private static ItemStack getTemplateStack() {
            return new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(HIGHEST_COMPRESSED_CLAY_TIER).get().asItem(), 64);
        }
    }
}
