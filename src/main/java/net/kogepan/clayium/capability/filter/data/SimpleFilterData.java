package net.kogepan.clayium.capability.filter.data;

import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.items.filter.FilterItemHelper;
import net.kogepan.clayium.items.filter.FilterSlotItemHandler;
import net.kogepan.clayium.registries.ClayiumFilterTypes;

import net.minecraft.world.item.ItemStack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Serializable payload for simple item filter data (items + whitelist flag).
 */
public class SimpleFilterData implements ItemFilterData {

    public static final MapCodec<SimpleFilterData> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ItemStack.OPTIONAL_CODEC.listOf().fieldOf("items").forGetter(SimpleFilterData::stacks),
                    Codec.BOOL.optionalFieldOf("whitelist", true).forGetter(SimpleFilterData::whitelist))
                    .apply(instance, SimpleFilterData::new));

    private final List<ItemStack> stacks;
    private final boolean whitelist;

    public SimpleFilterData(@NotNull List<ItemStack> stacks, boolean whitelist) {
        this.stacks = normalizeStacks(stacks);
        this.whitelist = whitelist;
    }

    @NotNull
    public static SimpleFilterData fromStack(@NotNull ItemStack stack) {
        return new SimpleFilterData(FilterItemHelper.readItems(stack), FilterItemHelper.getWhitelist(stack, true));
    }

    @NotNull
    public List<ItemStack> stacks() {
        return stacks;
    }

    public boolean whitelist() {
        return whitelist;
    }

    @Override
    @NotNull
    public ItemFilterType<?> type() {
        return ClayiumFilterTypes.SIMPLE.get();
    }

    @Override
    public boolean test(@NotNull ItemStack stack) {
        boolean match = stacks.stream().anyMatch(filterStack -> {
            ItemFilterData nested = filterStack.getCapability(ClayiumCapabilities.ITEM_FILTER_DATA);
            if (nested != null) {
                return nested.test(stack);
            }
            return ItemStack.isSameItemSameComponents(filterStack, stack);
        });
        return match == whitelist;
    }

    @NotNull
    private static List<ItemStack> normalizeStacks(@NotNull List<ItemStack> stacks) {
        List<ItemStack> normalized = new ArrayList<>(FilterSlotItemHandler.SLOTS);
        for (int i = 0; i < FilterSlotItemHandler.SLOTS; i++) {
            ItemStack source = i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY;
            if (source == null || source.isEmpty()) {
                normalized.add(ItemStack.EMPTY);
                continue;
            }
            ItemStack copy = source.copy();
            copy.setCount(1);
            normalized.add(copy);
        }
        return List.copyOf(normalized);
    }

    @NotNull
    private static List<ItemStack> copyStacks(@NotNull List<ItemStack> stacks) {
        List<ItemStack> copied = new ArrayList<>(stacks.size());
        for (ItemStack stack : stacks) {
            copied.add(stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }
        return copied;
    }
}
