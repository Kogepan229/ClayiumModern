package net.kogepan.clayium.items.filter;

import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.items.filter.component.FilterSlotsData;
import net.kogepan.clayium.registries.ClayiumDataComponents;

import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class FilterItemHelper {

    private FilterItemHelper() {}

    public static List<ItemStack> readItems(ItemStack stack) {
        FilterSlotsData data = stack.getOrDefault(
                ClayiumDataComponents.FILTER_SLOTS.get(),
                FilterSlotsData.EMPTY);
        return new ArrayList<>(data.createStacks());
    }

    public static void writeItems(ItemStack stack, List<ItemStack> stacks) {
        stack.set(ClayiumDataComponents.FILTER_SLOTS.get(), FilterSlotsData.fromStacks(stacks));
    }

    public static ItemStack getItem(ItemStack stack, int slot) {
        if (slot < 0 || slot >= FilterSlotsData.SLOT_COUNT) {
            return ItemStack.EMPTY;
        }
        return readItems(stack).get(slot);
    }

    public static void setItem(ItemStack stack, int slot, ItemStack value) {
        if (slot < 0 || slot >= FilterSlotsData.SLOT_COUNT) {
            return;
        }
        List<ItemStack> items = readItems(stack);
        items.set(slot, value.isEmpty() ? ItemStack.EMPTY : value.copyWithCount(1));
        writeItems(stack, items);
    }

    public static boolean getWhitelist(ItemStack stack) {
        return stack.getOrDefault(ClayiumDataComponents.FILTER_WHITELIST.get(), true);
    }

    public static void setWhitelist(ItemStack stack, boolean whitelist) {
        stack.set(ClayiumDataComponents.FILTER_WHITELIST.get(), whitelist);
    }

    public static String getFilterString(ItemStack stack) {
        return stack.getOrDefault(ClayiumDataComponents.FILTER_STRING.get(), "");
    }

    public static void setFilterString(ItemStack stack, String value) {
        stack.set(ClayiumDataComponents.FILTER_STRING.get(), value);
    }

    public static boolean hasCopyFlag(ItemStack stack) {
        return stack.getOrDefault(ClayiumDataComponents.FILTER_COPY_FLAG.get(), false);
    }

    public static void setCopyFlag(ItemStack stack, boolean copy) {
        stack.set(ClayiumDataComponents.FILTER_COPY_FLAG.get(), copy);
    }

    public static @Nullable ItemFilterData getCopiedFilterData(ItemStack stack) {
        return stack.get(ClayiumDataComponents.FILTER_COPIED_DATA.get());
    }

    public static void setCopiedFilterData(ItemStack stack, @Nullable ItemFilterData data) {
        if (data == null) {
            stack.remove(ClayiumDataComponents.FILTER_COPIED_DATA.get());
        } else {
            stack.set(ClayiumDataComponents.FILTER_COPIED_DATA.get(), data);
        }
    }
}
