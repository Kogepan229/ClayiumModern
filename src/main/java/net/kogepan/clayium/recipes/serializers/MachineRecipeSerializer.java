package net.kogepan.clayium.recipes.serializers;

import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.SimpleMachineRecipeType;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;

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

public class MachineRecipeSerializer implements RecipeSerializer<MachineRecipe> {

    public static final MapCodec<MachineRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SimpleMachineRecipeType.CODEC.fieldOf("recipe_type").forGetter(MachineRecipe::recipeType),
            ItemIngredientStack.CODEC.listOf().fieldOf("inputs").forGetter(MachineRecipe::inputs),
            ItemStack.CODEC.listOf().fieldOf("outputs").forGetter(MachineRecipe::outputs),
            Codec.LONG.fieldOf("duration").forGetter(MachineRecipe::duration),
            Codec.LONG.fieldOf("ce_per_tick").forGetter(MachineRecipe::cePerTick),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("recipe_tier").forGetter(MachineRecipe::recipeTier))
            .apply(instance, MachineRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MachineRecipe> STREAM_CODEC = StreamCodec.composite(
            SimpleMachineRecipeType.STREAM_CODEC, MachineRecipe::recipeType,
            ItemIngredientStack.STREAM_CODEC.apply(ByteBufCodecs.list()), MachineRecipe::inputs,
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), MachineRecipe::outputs,
            ByteBufCodecs.VAR_LONG, MachineRecipe::duration,
            ByteBufCodecs.VAR_LONG, MachineRecipe::cePerTick,
            ByteBufCodecs.VAR_INT, MachineRecipe::recipeTier,
            MachineRecipe::new);

    @Override
    @NotNull
    public MapCodec<MachineRecipe> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public StreamCodec<RegistryFriendlyByteBuf, MachineRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
