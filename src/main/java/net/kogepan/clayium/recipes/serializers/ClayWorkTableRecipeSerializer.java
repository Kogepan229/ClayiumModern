package net.kogepan.clayium.recipes.serializers;

import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.ClayWorkTableRecipe;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class ClayWorkTableRecipeSerializer implements RecipeSerializer<ClayWorkTableRecipe> {

    private static final Codec<Integer> BUTTON_CODEC = ExtraCodecs.intRange(0, 5);

    public static final MapCodec<ClayWorkTableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemIngredientStack.CODEC.fieldOf("input_item").forGetter(ClayWorkTableRecipe::ingredient),
            ItemStack.CODEC.fieldOf("result").forGetter(ClayWorkTableRecipe::result),
            ItemStack.CODEC.optionalFieldOf("byproduct", ItemStack.EMPTY).forGetter(ClayWorkTableRecipe::byproduct),
            BUTTON_CODEC.fieldOf("button").forGetter(ClayWorkTableRecipe::button),
            ExtraCodecs.POSITIVE_INT.fieldOf("cost").forGetter(ClayWorkTableRecipe::cost))
            .apply(instance, ClayWorkTableRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClayWorkTableRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemIngredientStack.STREAM_CODEC, ClayWorkTableRecipe::ingredient,
            ItemStack.STREAM_CODEC, ClayWorkTableRecipe::result,
            ItemStack.OPTIONAL_STREAM_CODEC, ClayWorkTableRecipe::byproduct,
            ByteBufCodecs.VAR_INT, ClayWorkTableRecipe::button,
            ByteBufCodecs.VAR_INT, ClayWorkTableRecipe::cost,
            ClayWorkTableRecipe::new);

    @Override
    @NotNull
    public MapCodec<ClayWorkTableRecipe> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public StreamCodec<RegistryFriendlyByteBuf, ClayWorkTableRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
