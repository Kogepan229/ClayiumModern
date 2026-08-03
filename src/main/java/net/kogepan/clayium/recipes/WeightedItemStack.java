package net.kogepan.clayium.recipes;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record WeightedItemStack(@NotNull ItemStack result, int weight) {

    public static final Codec<WeightedItemStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("result").forGetter(WeightedItemStack::result),
            ExtraCodecs.POSITIVE_INT.fieldOf("weight").forGetter(WeightedItemStack::weight))
            .apply(instance, WeightedItemStack::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WeightedItemStack> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, WeightedItemStack::result,
            ByteBufCodecs.VAR_INT, WeightedItemStack::weight,
            WeightedItemStack::new);

    public WeightedItemStack {
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Weighted result must not be empty");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
    }
}
