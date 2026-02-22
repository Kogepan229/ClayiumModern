package net.kogepan.clayium.capability.filter.data;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;

/**
 * Registered filter data type definition for item-applied filters.
 *
 * @param <D> concrete filter data type
 */
public class ItemFilterType<D extends ItemFilterData> {

    private final MapCodec<D> codec;

    public ItemFilterType(@NotNull MapCodec<D> codec) {
        this.codec = codec;
    }

    @NotNull
    public MapCodec<D> codec() {
        return codec;
    }
}
