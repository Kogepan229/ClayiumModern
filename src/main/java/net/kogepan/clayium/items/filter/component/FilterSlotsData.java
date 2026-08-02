package net.kogepan.clayium.items.filter.component;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import com.mojang.serialization.Codec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/** Immutable snapshots of the ten configurable item filter slots. */
public final class FilterSlotsData {

    public static final int SLOT_COUNT = 10;
    public static final FilterSlotsData EMPTY = new FilterSlotsData(
            Collections.nCopies(SLOT_COUNT, Optional.empty()));

    public static final Codec<FilterSlotsData> CODEC = ItemStack.OPTIONAL_CODEC
            .sizeLimitedListOf(SLOT_COUNT)
            .xmap(FilterSlotsData::fromStacks, FilterSlotsData::createStacks);

    public static final StreamCodec<RegistryFriendlyByteBuf, FilterSlotsData> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC
            .apply(ByteBufCodecs.list(SLOT_COUNT))
            .map(FilterSlotsData::fromStacks, FilterSlotsData::createStacks);

    private final List<Optional<ItemStackTemplate>> slots;
    private final int hashCode;

    private FilterSlotsData(List<Optional<ItemStackTemplate>> slots) {
        this.slots = List.copyOf(slots);
        this.hashCode = this.slots.hashCode();
    }

    public static FilterSlotsData fromStacks(List<ItemStack> stacks) {
        List<Optional<ItemStackTemplate>> snapshots = new ArrayList<>(SLOT_COUNT);
        boolean empty = true;
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            ItemStack stack = slot < stacks.size() ? stacks.get(slot) : ItemStack.EMPTY;
            if (stack.isEmpty()) {
                snapshots.add(Optional.empty());
            } else {
                snapshots.add(Optional.of(ItemStackTemplate.fromNonEmptyStack(stack).withCount(1)));
                empty = false;
            }
        }
        return empty ? EMPTY : new FilterSlotsData(snapshots);
    }

    public List<ItemStack> createStacks() {
        return this.slots.stream()
                .map(slot -> slot.map(ItemStackTemplate::create).orElse(ItemStack.EMPTY))
                .toList();
    }

    public Stream<ItemStackTemplate> nonEmptyTemplates() {
        return this.slots.stream().flatMap(Optional::stream);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof FilterSlotsData that && this.slots.equals(that.slots);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return "FilterSlotsData" + this.slots;
    }
}
