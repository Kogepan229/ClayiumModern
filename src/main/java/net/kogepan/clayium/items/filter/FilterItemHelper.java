package net.kogepan.clayium.items.filter;

import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.items.filter.component.FilterSlotsData;
import net.kogepan.clayium.registries.ClayiumDataComponents;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for reading/writing filter data via data components (filter slots, whitelist, filter
 * string, copy flag).
 */
public final class FilterItemHelper {

    private FilterItemHelper() {}

    /**
     * Returns the filter slot contents from the stack. Always returns a mutable list of size
     * {@link FilterSlotItemHandler#SLOTS}; empty slots are ItemStack.EMPTY.
     */
    @NotNull
    public static List<ItemStack> readItems(@NotNull ItemStack stack) {
        FilterSlotsData data = stack.getOrDefault(ClayiumDataComponents.FILTER_SLOTS.get(),
                FilterSlotsData.EMPTY);
        return new ArrayList<>(data.slots());
    }

    /**
     * Writes the filter slot contents to the stack. List is normalized to
     * {@link FilterSlotItemHandler#SLOTS} slots (pad with empty, trim excess).
     */
    public static void writeItems(@NotNull ItemStack stack, @NotNull List<ItemStack> stacks) {
        stack.set(ClayiumDataComponents.FILTER_SLOTS.get(), FilterSlotsData.fromList(stacks));
    }

    public static boolean getWhitelist(@NotNull ItemStack stack, boolean defaultValue) {
        return stack.getOrDefault(ClayiumDataComponents.FILTER_WHITELIST.get(), defaultValue);
    }

    public static void setWhitelist(@NotNull ItemStack stack, boolean whitelist) {
        stack.set(ClayiumDataComponents.FILTER_WHITELIST.get(), whitelist);
    }

    @NotNull
    public static String getFilterString(@NotNull ItemStack stack) {
        return stack.getOrDefault(ClayiumDataComponents.FILTER_STRING.get(), "");
    }

    public static void setFilterString(@NotNull ItemStack stack, @NotNull String value) {
        stack.set(ClayiumDataComponents.FILTER_STRING.get(), value);
    }

    public static boolean hasCopyFlag(@NotNull ItemStack stack) {
        return stack.getOrDefault(ClayiumDataComponents.FILTER_COPY_FLAG.get(), false);
    }

    public static void setCopyFlag(@NotNull ItemStack stack, boolean copy) {
        if (copy) {
            stack.set(ClayiumDataComponents.FILTER_COPY_FLAG.get(), true);
        } else {
            stack.remove(ClayiumDataComponents.FILTER_COPY_FLAG.get());
        }
    }

    @Nullable
    public static ItemFilterData getCopiedFilterData(@NotNull ItemStack stack) {
        return stack.get(ClayiumDataComponents.FILTER_COPIED_DATA.get());
    }

    public static void setCopiedFilterData(@NotNull ItemStack stack, @Nullable ItemFilterData data) {
        if (data == null) {
            stack.remove(ClayiumDataComponents.FILTER_COPIED_DATA.get());
        } else {
            stack.set(ClayiumDataComponents.FILTER_COPIED_DATA.get(), data);
        }
    }
}
