package net.kogepan.clayium.capability.filter.data;

import net.minecraft.core.Registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public final class ItemFilterDataCodecs {

    private ItemFilterDataCodecs() {}

    public static Codec<ItemFilterData> dispatchCodec(Registry<ItemFilterType<?>> registry) {
        return registry.byNameCodec().dispatch(ItemFilterData::type, ItemFilterDataCodecs::mapCodecForType);
    }

    @SuppressWarnings("unchecked")
    private static MapCodec<ItemFilterData> mapCodecForType(ItemFilterType<?> type) {
        return (MapCodec<ItemFilterData>) type.codec();
    }
}
