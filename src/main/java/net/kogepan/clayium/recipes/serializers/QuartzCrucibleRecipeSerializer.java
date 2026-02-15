package net.kogepan.clayium.recipes.serializers;

import net.kogepan.clayium.recipes.recipes.QuartzCrucibleRecipe;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class QuartzCrucibleRecipeSerializer implements RecipeSerializer<QuartzCrucibleRecipe> {

    public static final MapCodec<QuartzCrucibleRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("input").forGetter(QuartzCrucibleRecipe::input),
            Ingredient.CODEC.fieldOf("catalyst").forGetter(QuartzCrucibleRecipe::catalyst),
            ItemStack.CODEC.fieldOf("result").forGetter(QuartzCrucibleRecipe::result),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("duration").forGetter(QuartzCrucibleRecipe::duration))
            .apply(instance, QuartzCrucibleRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuartzCrucibleRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, QuartzCrucibleRecipe::input,
            Ingredient.CONTENTS_STREAM_CODEC, QuartzCrucibleRecipe::catalyst,
            ItemStack.STREAM_CODEC, QuartzCrucibleRecipe::result,
            ByteBufCodecs.VAR_INT, QuartzCrucibleRecipe::duration,
            QuartzCrucibleRecipe::new);

    @Override
    @NotNull
    public MapCodec<QuartzCrucibleRecipe> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public StreamCodec<RegistryFriendlyByteBuf, QuartzCrucibleRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
