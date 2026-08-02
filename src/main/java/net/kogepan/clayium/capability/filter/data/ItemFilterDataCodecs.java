package net.kogepan.clayium.capability.filter.data;

import net.minecraft.core.Registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

/** Codec factories for registry-dispatched item filter payloads. */
public final class ItemFilterDataCodecs {

    private ItemFilterDataCodecs() {}

    /** Creates the flattened form used by the copied-filter data component. */
    public static Codec<ItemFilterData> dispatchCodec(Registry<ItemFilterType<?>> registry) {
        return registry.byNameCodec().dispatch(ItemFilterData::type, ItemFilterDataCodecs::mapCodecForType);
    }

    /** Creates the legacy-compatible {@code {type, data}} form used by block entity persistence. */
    public static Codec<ItemFilterData> persistedCodec(Registry<ItemFilterType<?>> registry) {
        return registry.byNameCodec().dispatch(
                "type",
                ItemFilterData::type,
                type -> mapCodecForType(type).codec().fieldOf("data"));
    }

    @SuppressWarnings("unchecked")
    private static MapCodec<ItemFilterData> mapCodecForType(ItemFilterType<?> type) {
        return (MapCodec<ItemFilterData>) type.codec();
    }
}
