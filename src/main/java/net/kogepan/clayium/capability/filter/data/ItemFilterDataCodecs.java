package net.kogepan.clayium.capability.filter.data;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.registries.ClayiumFilterTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Codec and NBT utilities for item filter data.
 */
public final class ItemFilterDataCodecs {

    private static final String KEY_TYPE = "type";
    private static final String KEY_DATA = "data";

    private ItemFilterDataCodecs() {}

    /**
     * Creates a dispatch codec for this registry.
     * The encoded shape is a map with the "type" discriminator and type-specific fields.
     */
    @NotNull
    public static Codec<ItemFilterData> dispatchCodec(@NotNull Registry<ItemFilterType<?>> registry) {
        Codec<ItemFilterType<?>> typeCodec = typeCodec(registry);
        return typeCodec.dispatch(ItemFilterData::type, ItemFilterDataCodecs::mapCodecForType);
    }

    /**
     * Encodes a filter data object to {type, data} compound.
     */
    @Nullable
    public static CompoundTag encode(@NotNull HolderLookup.Provider provider, @NotNull ItemFilterData data) {
        Registry<ItemFilterType<?>> registry = ClayiumFilterTypes.FILTER_TYPES.getRegistry().get();
        ResourceLocation typeId = registry.getKey(data.type());
        if (typeId == null) {
            Clayium.LOGGER.warn("Tried to encode unregistered item filter type: {}", data.type());
            return null;
        }

        Tag encodedData = codecForType(data.type())
                .encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), data)
                .resultOrPartial(error -> Clayium.LOGGER.warn("Failed to encode item filter data {}: {}",
                        typeId, error))
                .orElse(null);
        if (!(encodedData instanceof CompoundTag dataTag)) {
            Clayium.LOGGER.warn("Encoded item filter data is not a compound: {}", typeId);
            return null;
        }

        CompoundTag tag = new CompoundTag();
        tag.putString(KEY_TYPE, typeId.toString());
        tag.put(KEY_DATA, dataTag);
        return tag;
    }

    /**
     * Decodes a filter data object from {type, data} compound.
     */
    @Nullable
    public static ItemFilterData decode(@NotNull HolderLookup.Provider provider, @NotNull CompoundTag tag) {
        if (!tag.contains(KEY_TYPE, Tag.TAG_STRING) || !tag.contains(KEY_DATA, Tag.TAG_COMPOUND)) {
            return null;
        }

        ResourceLocation typeId = ResourceLocation.tryParse(tag.getString(KEY_TYPE));
        if (typeId == null) {
            Clayium.LOGGER.warn("Invalid item filter type id '{}'", tag.getString(KEY_TYPE));
            return null;
        }

        Registry<ItemFilterType<?>> registry = ClayiumFilterTypes.FILTER_TYPES.getRegistry().get();
        ItemFilterType<?> type = registry.get(typeId);
        if (type == null) {
            Clayium.LOGGER.warn("Unknown item filter type '{}'", typeId);
            return null;
        }

        return codecForType(type)
                .parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get(KEY_DATA))
                .resultOrPartial(error -> Clayium.LOGGER.warn("Failed to decode item filter data {}: {}",
                        typeId, error))
                .orElse(null);
    }

    @NotNull
    private static Codec<ItemFilterType<?>> typeCodec(@NotNull Registry<ItemFilterType<?>> registry) {
        return ResourceLocation.CODEC.flatXmap(
                id -> {
                    ItemFilterType<?> type = registry.get(id);
                    if (type == null) {
                        return DataResult.error(() -> "Unknown item filter type: " + id);
                    }
                    return DataResult.success(type);
                },
                type -> {
                    ResourceLocation id = registry.getKey(type);
                    if (id == null) {
                        return DataResult.error(() -> "Unregistered item filter type: " + type);
                    }
                    return DataResult.success(id);
                });
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private static Codec<ItemFilterData> codecForType(@NotNull ItemFilterType<?> type) {
        return (Codec<ItemFilterData>) type.codec().codec();
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private static MapCodec<ItemFilterData> mapCodecForType(@NotNull ItemFilterType<?> type) {
        return (MapCodec<ItemFilterData>) type.codec();
    }
}
