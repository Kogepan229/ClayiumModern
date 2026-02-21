package net.kogepan.clayium.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Capability interface for block entities that can have item filters applied per face.
 * Used when the player applies a filter item to a ClayContainer face.
 */
public interface IItemFilterApplicatable {

    /**
     * Sets the filter for the given face. The filter and stack are stored for persistence.
     *
     * @param side        the face to apply the filter to
     * @param filter      the filter instance (used for runtime filtering)
     * @param filterStack copy of the filter item stack (used for persistence and createFilterStack)
     */
    void setFilter(@NotNull Direction side, @NotNull IItemFilter filter, @NotNull ItemStack filterStack);

    /**
     * Returns the filter for the given face, or null if none is set.
     */
    @Nullable
    IItemFilter getFilter(@NotNull Direction side);

    /**
     * Returns a copy of the filter stack stored for the given face, for filter duplicator use.
     * Returns null if no filter is set for that face.
     */
    @Nullable
    ItemStack createFilterStack(@NotNull Direction side);

    /**
     * Clears the filter for the given face.
     */
    void clearFilter(@NotNull Direction side);
}
