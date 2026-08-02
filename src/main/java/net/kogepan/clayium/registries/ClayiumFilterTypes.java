package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.capability.filter.data.FuzzyFilterData;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.capability.filter.data.ItemFilterDataCodecs;
import net.kogepan.clayium.capability.filter.data.ItemFilterType;
import net.kogepan.clayium.capability.filter.data.SimpleFilterData;
import net.kogepan.clayium.capability.filter.data.UnlocalizedNameFilterData;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.mojang.serialization.Codec;

/** Registry for item filter payload types. */
public final class ClayiumFilterTypes {

    public static final ResourceKey<Registry<ItemFilterType<?>>> FILTER_TYPE_REGISTRY_KEY = ResourceKey
            .createRegistryKey(Clayium.id("item_filter_type"));

    public static final DeferredRegister<ItemFilterType<?>> FILTER_TYPES = DeferredRegister
            .create(FILTER_TYPE_REGISTRY_KEY, Clayium.MODID);

    private static final Registry<ItemFilterType<?>> FILTER_TYPE_REGISTRY = FILTER_TYPES
            .makeRegistry(builder -> {});

    public static final DeferredHolder<ItemFilterType<?>, ItemFilterType<SimpleFilterData>> SIMPLE = FILTER_TYPES
            .register("simple", () -> new ItemFilterType<>(SimpleFilterData.CODEC));

    public static final DeferredHolder<ItemFilterType<?>, ItemFilterType<FuzzyFilterData>> FUZZY = FILTER_TYPES
            .register("fuzzy", () -> new ItemFilterType<>(FuzzyFilterData.CODEC));

    public static final DeferredHolder<ItemFilterType<?>, ItemFilterType<UnlocalizedNameFilterData>> UNLOCALIZED_NAME = FILTER_TYPES
            .register("unlocalized_name", () -> new ItemFilterType<>(UnlocalizedNameFilterData.CODEC));

    public static final Codec<ItemFilterData> FILTER_DATA_CODEC = ItemFilterDataCodecs
            .dispatchCodec(FILTER_TYPE_REGISTRY);

    public static final Codec<ItemFilterData> PERSISTED_FILTER_DATA_CODEC = ItemFilterDataCodecs
            .persistedCodec(FILTER_TYPE_REGISTRY);

    private ClayiumFilterTypes() {}
}
