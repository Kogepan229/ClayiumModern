package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.capability.IItemFilterApplicatable;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.capability.filter.data.ItemFilterDataCodecs;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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

    private final ItemFilterData[] filterData = new ItemFilterData[6];

    /**
     * Client-only: which sides have a filter (for rendering). Synced via getUpdateTag/handleUpdateTag.
     */
    private final boolean[] clientFilterFlags = new boolean[6];

    public ItemFilterHolderTrait(@NotNull ClayContainerBlockEntity blockEntity) {
        super(blockEntity, TRAIT_ID);
    }

    @Override
    public void setFilter(@NotNull Direction side, @NotNull ItemFilterData data) {
        int i = side.ordinal();
        filterData[i] = data;
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
    public ItemFilterData getFilter(@NotNull Direction side) {
        return filterData[side.ordinal()];
    }

    @Override
    public void clearFilter(@NotNull Direction side) {
        int i = side.ordinal();
        filterData[i] = null;
        blockEntity.setChanged();
        clientFilterFlags[i] = false;
    }

    /**
     * Client-only: returns whether the given side has a filter (for rendering).
     */
    public boolean hasFilterClientOnly(@NotNull Direction side) {
        return clientFilterFlags[side.ordinal()];
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        for (int i = 0; i < 6; i++) {
            if (filterData[i] == null) {
                continue;
            }
            CompoundTag encoded = ItemFilterDataCodecs.encode(provider, filterData[i]);
            if (encoded == null) {
                Clayium.LOGGER.warn("Failed to save item filter at {} side {}",
                        blockEntity.getBlockPos(), Direction.from3DDataValue(i));
                continue;
            }
            tag.put(FILTER_PREFIX + i, encoded);
        }
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        for (int i = 0; i < 6; i++) {
            String key = FILTER_PREFIX + i;
            if (!tag.contains(key, Tag.TAG_COMPOUND)) {
                filterData[i] = null;
                continue;
            }
            ItemFilterData decoded = ItemFilterDataCodecs.decode(provider, tag.getCompound(key));
            if (decoded == null) {
                Clayium.LOGGER.warn("Failed to load item filter at {} side {}",
                        blockEntity.getBlockPos(), Direction.from3DDataValue(i));
                filterData[i] = null;
                continue;
            }
            filterData[i] = decoded;
        }
        for (int i = 0; i < 6; i++) {
            clientFilterFlags[i] = filterData[i] != null;
        }
    }

    @Override
    public void saveForUpdate(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        byte[] flags = new byte[6];
        for (int i = 0; i < 6; i++) {
            flags[i] = (byte) (filterData[i] != null ? 1 : 0);
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
