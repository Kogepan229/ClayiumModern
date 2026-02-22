package net.kogepan.clayium.capability.filter.data;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

/**
 * Serializable filter payload stored in ClayContainer sides.
 */
public interface ItemFilterData {

    /**
     * Returns the registered type of this data.
     */
    @NotNull
    ItemFilterType<?> type();

    /**
     * Tests whether the given stack matches this filter.
     */
    boolean test(@NotNull ItemStack stack);
}
