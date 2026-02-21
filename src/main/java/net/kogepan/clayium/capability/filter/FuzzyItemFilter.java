package net.kogepan.clayium.capability.filter;

import net.kogepan.clayium.capability.IItemFilter;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Filter that matches stacks by item type only (ignores NBT and damage).
 * Plan option A: same item via ItemStack.isSameItem only; no tags.
 */
public class FuzzyItemFilter implements IItemFilter {

    private final List<ItemStack> stacks;

    public FuzzyItemFilter(List<ItemStack> stacks) {
        this.stacks = stacks;
    }

    @Override
    public boolean test(@NotNull ItemStack stack) {
        return stacks.stream().anyMatch(filterStack -> ItemStack.isSameItem(filterStack, stack));
    }
}
