package net.kogepan.clayium.capability.filter.data;

import com.mojang.serialization.MapCodec;

/** Registered codec definition for an item filter payload. */
public record ItemFilterType<D extends ItemFilterData>(MapCodec<D> codec) {}
