package net.kogepan.clayium.capability;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

/**
 * Predicate-like interface for filtering item stacks.
 * Used by filter items (Simple, Fuzzy, UnlocalizedName) to test whether a stack matches.
 */
@FunctionalInterface
public interface IItemFilter {

    /**
     * Returns whether the given stack passes this filter.
     * The stack must not be modified.
     *
     * @param stack the stack to test
     * @return true if the stack matches the filter
     */
    boolean test(@NotNull ItemStack stack);
}
