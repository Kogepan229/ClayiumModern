package net.kogepan.clayium.capability.filter;

import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.IItemFilter;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Filter that matches stacks against a list of items. Supports whitelist/blacklist mode.
 * If a list entry is itself a filter item, it is resolved via IItemFilter capability.
 */
public class SimpleItemFilter implements IItemFilter {

    private final List<ItemStack> stacks;
    private final boolean whitelist;

    public SimpleItemFilter(List<ItemStack> stacks, boolean whitelist) {
        this.stacks = stacks;
        this.whitelist = whitelist;
    }

    @Override
    public boolean test(@NotNull ItemStack stack) {
        boolean match = stacks.stream().anyMatch(filterStack -> {
            IItemFilter nested = filterStack.getCapability(ClayiumCapabilities.ITEM_FILTER);
            if (nested != null) {
                return nested.test(stack);
            }
            return ItemStack.isSameItemSameComponents(filterStack, stack);
        });
        return match == whitelist;
    }
}
