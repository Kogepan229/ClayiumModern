package net.kogepan.clayium.items.filter.component;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import com.mojang.serialization.Codec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds exactly 10 item stacks for Simple/Fuzzy filter slots. Immutable.
 */
public record FilterSlotsData(List<ItemStack> slots) {

    public static final int SLOT_COUNT = 10;

    public static final FilterSlotsData EMPTY = new FilterSlotsData(
            Collections.nCopies(SLOT_COUNT, ItemStack.EMPTY));

    public FilterSlotsData {
        if (slots.size() != SLOT_COUNT) {
            List<ItemStack> copy = new ArrayList<>(SLOT_COUNT);
            for (int i = 0; i < SLOT_COUNT; i++) {
                copy.add(i < slots.size() ? slots.get(i) : ItemStack.EMPTY);
            }
            slots = Collections.unmodifiableList(copy);
        } else {
            slots = List.copyOf(slots);
        }
    }

    public static FilterSlotsData fromList(List<ItemStack> list) {
        if (list == null || list.isEmpty()) {
            return EMPTY;
        }
        return new FilterSlotsData(list);
    }

    public static final Codec<FilterSlotsData> CODEC = ItemStack.OPTIONAL_CODEC
            .listOf()
            .xmap(FilterSlotsData::fromList, FilterSlotsData::slots);

    public static final StreamCodec<RegistryFriendlyByteBuf, FilterSlotsData> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(FilterSlotsData::fromList, FilterSlotsData::slots);
}
