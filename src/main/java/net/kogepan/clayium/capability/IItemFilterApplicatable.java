package net.kogepan.clayium.capability;

import net.kogepan.clayium.capability.filter.data.ItemFilterData;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Capability interface for block entities that can have item filters applied per face.
 * Used when the player applies a filter item to a ClayContainer face.
 */
public interface IItemFilterApplicatable {

    /**
     * Sets the filter for the given face from serializable filter data.
     *
     * @param side       the face to apply the filter to
     * @param filterData serializable filter payload used for persistence/runtime/copy
     */
    void setFilter(@NotNull Direction side, @NotNull ItemFilterData filterData);

    /**
     * Returns the filter for the given face, or null if none is set.
     */
    @Nullable
    ItemFilterData getFilter(@NotNull Direction side);

    /**
     * Clears the filter for the given face.
     */
    void clearFilter(@NotNull Direction side);
}
