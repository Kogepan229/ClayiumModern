package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.capability.filter.data.FuzzyFilterData;
import net.kogepan.clayium.capability.filter.data.ItemFilterType;
import net.kogepan.clayium.capability.filter.data.SimpleFilterData;
import net.kogepan.clayium.capability.filter.data.UnlocalizedNameFilterData;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for item-side filter data types.
 */
public final class ClayiumFilterTypes {

    public static final ResourceKey<Registry<ItemFilterType<?>>> FILTER_TYPE_REGISTRY_KEY = ResourceKey
            .createRegistryKey(Clayium.id("item_filter_type"));

    public static final DeferredRegister<ItemFilterType<?>> FILTER_TYPES = DeferredRegister
            .create(FILTER_TYPE_REGISTRY_KEY, Clayium.MODID);

    @SuppressWarnings("unused")
    private static final Registry<ItemFilterType<?>> FILTER_TYPE_REGISTRY = FILTER_TYPES
            .makeRegistry(builder -> {});

    public static final DeferredHolder<ItemFilterType<?>, ItemFilterType<SimpleFilterData>> SIMPLE = FILTER_TYPES
            .register("simple", () -> new ItemFilterType<>(SimpleFilterData.CODEC));

    public static final DeferredHolder<ItemFilterType<?>, ItemFilterType<FuzzyFilterData>> FUZZY = FILTER_TYPES
            .register("fuzzy", () -> new ItemFilterType<>(FuzzyFilterData.CODEC));

    public static final DeferredHolder<ItemFilterType<?>, ItemFilterType<UnlocalizedNameFilterData>> UNLOCALIZED_NAME = FILTER_TYPES
            .register("unlocalized_name", () -> new ItemFilterType<>(UnlocalizedNameFilterData.CODEC));

    private ClayiumFilterTypes() {}
}
