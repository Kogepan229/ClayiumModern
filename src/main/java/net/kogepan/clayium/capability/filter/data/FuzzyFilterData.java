package net.kogepan.clayium.capability.filter.data;

import net.kogepan.clayium.items.filter.component.FilterSlotsData;
import net.kogepan.clayium.registries.ClayiumFilterTypes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

/** Serializable payload for an item-and-common-tag fuzzy filter. */
public record FuzzyFilterData(FilterSlotsData slots) implements ItemFilterData {

    public static final MapCodec<FuzzyFilterData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(FilterSlotsData.CODEC.fieldOf("items").forGetter(FuzzyFilterData::slots))
            .apply(instance, FuzzyFilterData::new));

    public FuzzyFilterData(List<ItemStack> stacks) {
        this(FilterSlotsData.fromStacks(stacks));
    }

    public List<ItemStack> stacks() {
        return this.slots.createStacks();
    }

    @Override
    public ItemFilterType<?> type() {
        return ClayiumFilterTypes.FUZZY.get();
    }

    @Override
    public boolean test(ItemStack stack) {
        return this.slots.nonEmptyTemplates().anyMatch(template -> matches(template, stack));
    }

    private static boolean matches(ItemStackTemplate template, ItemStack stack) {
        if (ItemStack.isSameItem(stack, template)) {
            return true;
        }
        return template.tags()
                .filter(tag -> "c".equals(tag.location().getNamespace()))
                .anyMatch(stack::is);
    }
}
