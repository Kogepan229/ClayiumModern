package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.items.filter.component.FilterSlotsData;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.mojang.serialization.Codec;

public final class ClayiumDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister
            .createDataComponents(Registries.DATA_COMPONENT_TYPE, Clayium.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FilterSlotsData>> FILTER_SLOTS = DATA_COMPONENTS
            .registerComponentType("filter_slots", builder -> builder
                    .persistent(FilterSlotsData.CODEC)
                    .networkSynchronized(FilterSlotsData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FILTER_WHITELIST = DATA_COMPONENTS
            .registerComponentType("filter_whitelist", builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> FILTER_STRING = DATA_COMPONENTS
            .registerComponentType("filter_string", builder -> builder
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FILTER_COPY_FLAG = DATA_COMPONENTS
            .registerComponentType("filter_copy_flag", builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemFilterData>> FILTER_COPIED_DATA = DATA_COMPONENTS
            .registerComponentType("filter_copied_data", builder -> builder
                    .persistent(ClayiumFilterTypes.FILTER_DATA_CODEC)
                    .networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(
                            ClayiumFilterTypes.FILTER_DATA_CODEC)));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> SYNCHRONIZER_TARGET = DATA_COMPONENTS
            .registerComponentType("synchronizer_target", builder -> builder
                    .persistent(GlobalPos.CODEC)
                    .networkSynchronized(GlobalPos.STREAM_CODEC));

    private ClayiumDataComponents() {}
}
