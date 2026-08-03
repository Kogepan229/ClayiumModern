package net.kogepan.clayium.recipes.serializers;

import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.WeightedItemStack;
import net.kogepan.clayium.recipes.recipes.WeightedMachineRecipe;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.RecipeSerializer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class WeightedMachineRecipeSerializer implements RecipeSerializer<WeightedMachineRecipe> {

    public static final MapCodec<WeightedMachineRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemIngredientStack.CODEC.listOf().fieldOf("inputs").forGetter(WeightedMachineRecipe::inputs),
            ExtraCodecs.nonEmptyList(WeightedItemStack.CODEC.listOf()).fieldOf("weighted_outputs")
                    .forGetter(WeightedMachineRecipe::weightedOutputs),
            Codec.LONG.fieldOf("duration").forGetter(WeightedMachineRecipe::duration),
            Codec.LONG.fieldOf("ce_per_tick").forGetter(WeightedMachineRecipe::cePerTick),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("recipe_tier").forGetter(WeightedMachineRecipe::recipeTier))
            .apply(instance, WeightedMachineRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WeightedMachineRecipe> STREAM_CODEC = StreamCodec
            .composite(
                    ItemIngredientStack.STREAM_CODEC.apply(ByteBufCodecs.list()), WeightedMachineRecipe::inputs,
                    WeightedItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), WeightedMachineRecipe::weightedOutputs,
                    ByteBufCodecs.VAR_LONG, WeightedMachineRecipe::duration,
                    ByteBufCodecs.VAR_LONG, WeightedMachineRecipe::cePerTick,
                    ByteBufCodecs.VAR_INT, WeightedMachineRecipe::recipeTier,
                    WeightedMachineRecipe::new);

    @Override
    @NotNull
    public MapCodec<WeightedMachineRecipe> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public StreamCodec<RegistryFriendlyByteBuf, WeightedMachineRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
