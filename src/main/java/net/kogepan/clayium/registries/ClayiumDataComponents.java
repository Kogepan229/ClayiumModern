package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.capability.filter.data.ItemFilterDataCodecs;
import net.kogepan.clayium.items.filter.component.FilterSlotsData;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.mojang.serialization.Codec;

/**
 * Custom data component types for filter items and other item-stack data.
 */
public class ClayiumDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister
            .create(Registries.DATA_COMPONENT_TYPE, Clayium.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FilterSlotsData>> FILTER_SLOTS = DATA_COMPONENTS
            .register("filter_slots",
                    () -> DataComponentType.<FilterSlotsData>builder()
                            .persistent(FilterSlotsData.CODEC)
                            .networkSynchronized(FilterSlotsData.STREAM_CODEC)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FILTER_WHITELIST = DATA_COMPONENTS
            .register("filter_whitelist",
                    () -> DataComponentType.<Boolean>builder()
                            .persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> FILTER_STRING = DATA_COMPONENTS
            .register("filter_string",
                    () -> DataComponentType.<String>builder()
                            .persistent(Codec.STRING)
                            .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FILTER_COPY_FLAG = DATA_COMPONENTS
            .register("filter_copy_flag",
                    () -> DataComponentType.<Boolean>builder()
                            .persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemFilterData>> FILTER_COPIED_DATA = DATA_COMPONENTS
            .register("filter_copied_data",
                    () -> DataComponentType.<ItemFilterData>builder()
                            .persistent(ItemFilterDataCodecs
                                    .dispatchCodec(ClayiumFilterTypes.FILTER_TYPES.getRegistry().get()))
                            .build());

    private ClayiumDataComponents() {}
}
