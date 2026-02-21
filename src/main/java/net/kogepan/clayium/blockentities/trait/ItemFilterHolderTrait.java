package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.capability.IItemFilter;
import net.kogepan.clayium.capability.IItemFilterApplicatable;
import net.kogepan.clayium.items.filter.ItemFilterBase;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Trait that holds per-face item filters for a ClayContainer. Implements IItemFilterApplicatable
 * so that filter items can be applied when the player uses them on a block face.
 */
public class ItemFilterHolderTrait extends ClayContainerTrait implements IItemFilterApplicatable {

    public static final String TRAIT_ID = "itemFilterHolder";

    private static final String FILTER_PREFIX = "filter";
    private static final String FILTER_FLAGS = "filterFlags";

    private final IItemFilter[] filters = new IItemFilter[6];
    private final ItemStack[] filterStacks = new ItemStack[6];

    /**
     * Client-only: which sides have a filter (for rendering). Synced via getUpdateTag/handleUpdateTag.
     */
    private final boolean[] clientFilterFlags = new boolean[6];

    public ItemFilterHolderTrait(@NotNull ClayContainerBlockEntity blockEntity) {
        super(blockEntity, TRAIT_ID);
        for (int i = 0; i < 6; i++) {
            filterStacks[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public void setFilter(@NotNull Direction side, @NotNull IItemFilter filter, @NotNull ItemStack filterStack) {
        int i = side.ordinal();
        filters[i] = filter;
        filterStacks[i] = filterStack.copy();
        clientFilterFlags[i] = true;

        blockEntity.setChanged();
        Level level = blockEntity.getLevel();
        if (level != null) {
            level.sendBlockUpdated(blockEntity.getBlockPos(), this.blockEntity.getBlockState(),
                    this.blockEntity.getBlockState(),
                    Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
        }
    }

    @Override
    @Nullable
    public IItemFilter getFilter(@NotNull Direction side) {
        return filters[side.ordinal()];
    }

    @Override
    @Nullable
    public ItemStack createFilterStack(@NotNull Direction side) {
        ItemStack stack = filterStacks[side.ordinal()];
        return stack.isEmpty() ? null : stack.copy();
    }

    @Override
    public void clearFilter(@NotNull Direction side) {
        int i = side.ordinal();
        filters[i] = null;
        filterStacks[i] = ItemStack.EMPTY;
        blockEntity.setChanged();
        clientFilterFlags[i] = false;
    }

    /**
     * Client-only: returns whether the given side has a filter (for rendering).
     */
    public boolean hasFilterClientOnly(@NotNull Direction side) {
        return clientFilterFlags[side.ordinal()];
    }

    /**
     * Writes only the "has filter" flags into the given tag for client sync.
     */
    public void writeFilterFlagsToTag(@NotNull CompoundTag tag) {
        byte[] flags = new byte[6];
        for (int i = 0; i < 6; i++) {
            flags[i] = (byte) (filters[i] != null ? 1 : 0);
        }
        tag.putByteArray(FILTER_FLAGS, flags);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        for (int i = 0; i < 6; i++) {
            if (filterStacks[i].isEmpty()) {
                continue;
            }
            // ItemStack.save() returns the encoded tag; it does not write into the passed tag (Codec contract)
            Tag stackTag = filterStacks[i].save(provider);
            tag.put(FILTER_PREFIX + i, stackTag);
        }
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        for (int i = 0; i < 6; i++) {
            String key = FILTER_PREFIX + i;
            if (!tag.contains(key)) {
                filters[i] = null;
                filterStacks[i] = ItemStack.EMPTY;
                continue;
            }
            ItemStack stack = ItemStack.parse(provider, tag.getCompound(key)).orElse(ItemStack.EMPTY);
            filterStacks[i] = stack;
            if (stack.isEmpty()) {
                filters[i] = null;
            } else if (stack.getItem() instanceof ItemFilterBase filterItem) {
                filters[i] = filterItem.createFilter(stack);
            } else {
                filters[i] = null;
            }
        }
        for (int i = 0; i < 6; i++) {
            clientFilterFlags[i] = filters[i] != null;
        }
    }

    @Override
    public void saveForUpdate(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        byte[] flags = new byte[6];
        for (int i = 0; i < 6; i++) {
            flags[i] = (byte) (filters[i] != null ? 1 : 0);
        }
        tag.putByteArray(FILTER_FLAGS, flags);
    }

    @Override
    public void loadForUpdate(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        if (tag.contains(FILTER_FLAGS)) {
            byte[] flags = tag.getByteArray(FILTER_FLAGS);
            for (int i = 0; i < 6 && i < flags.length; i++) {
                clientFilterFlags[i] = flags[i] != 0;
            }
        }
    }

    @Override
    public void tick() {
        // No per-tick logic
    }
}
