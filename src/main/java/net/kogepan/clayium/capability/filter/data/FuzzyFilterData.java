package net.kogepan.clayium.capability.filter.data;

import net.kogepan.clayium.items.filter.FilterItemHelper;
import net.kogepan.clayium.items.filter.FilterSlotItemHandler;
import net.kogepan.clayium.registries.ClayiumFilterTypes;

import net.minecraft.world.item.ItemStack;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Serializable payload for fuzzy item filter data (items only).
 */
public class FuzzyFilterData implements ItemFilterData {

    public static final MapCodec<FuzzyFilterData> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ItemStack.OPTIONAL_CODEC.listOf().fieldOf("items").forGetter(FuzzyFilterData::stacks))
                    .apply(instance, FuzzyFilterData::new));

    private final List<ItemStack> stacks;

    public FuzzyFilterData(@NotNull List<ItemStack> stacks) {
        this.stacks = normalizeStacks(stacks);
    }

    @NotNull
    public static FuzzyFilterData fromStack(@NotNull ItemStack stack) {
        return new FuzzyFilterData(FilterItemHelper.readItems(stack));
    }

    @NotNull
    public List<ItemStack> stacks() {
        return stacks;
    }

    @Override
    @NotNull
    public ItemFilterType<?> type() {
        return ClayiumFilterTypes.FUZZY.get();
    }

    @Override
    public boolean test(@NotNull ItemStack stack) {
        return stacks.stream().anyMatch(filterStack -> ItemStack.isSameItem(filterStack, stack));
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
}
