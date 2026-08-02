package net.kogepan.clayium.capability.filter.data;

import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.items.filter.component.FilterSlotsData;
import net.kogepan.clayium.registries.ClayiumFilterTypes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

/** Serializable payload for an exact whitelist or blacklist filter. */
public record SimpleFilterData(FilterSlotsData slots, boolean whitelist) implements ItemFilterData {

    public static final MapCodec<SimpleFilterData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    FilterSlotsData.CODEC.fieldOf("items").forGetter(SimpleFilterData::slots),
                    Codec.BOOL.optionalFieldOf("whitelist", true).forGetter(SimpleFilterData::whitelist))
            .apply(instance, SimpleFilterData::new));

    public SimpleFilterData(List<ItemStack> stacks, boolean whitelist) {
        this(FilterSlotsData.fromStacks(stacks), whitelist);
    }

    public List<ItemStack> stacks() {
        return this.slots.createStacks();
    }

    @Override
    public ItemFilterType<?> type() {
        return ClayiumFilterTypes.SIMPLE.get();
    }

    @Override
    public boolean test(ItemStack stack) {
        boolean matches = this.slots.nonEmptyTemplates().anyMatch(template -> matches(template, stack));
        return matches == this.whitelist;
    }

    private static boolean matches(ItemStackTemplate template, ItemStack stack) {
        ItemFilterData nestedFilter = template.create().getCapability(ClayiumCapabilities.ITEM_FILTER_DATA);
        return nestedFilter != null ? nestedFilter.test(stack) : ItemStack.isSameItemSameComponents(stack, template);
    }
}
