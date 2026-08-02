package net.kogepan.clayium.recipes.recipes;

import net.kogepan.clayium.recipes.ClayiumRecipeSerializers;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.display.ClayiumRecipeBookCategories;
import net.kogepan.clayium.recipes.display.LaserIrradiationRecipeDisplay;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record LaserIrradiationRecipe(Block inputBlock, Block outputBlock, long requiredEnergy)
        implements Recipe<SingleRecipeInput> {

    public static final MapCodec<LaserIrradiationRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("input")
                            .forGetter(LaserIrradiationRecipe::inputBlock),
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("output")
                            .forGetter(LaserIrradiationRecipe::outputBlock),
                    Codec.LONG.fieldOf("required_energy").forGetter(LaserIrradiationRecipe::requiredEnergy))
            .apply(instance, LaserIrradiationRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LaserIrradiationRecipe> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.registry(Registries.BLOCK),
                    LaserIrradiationRecipe::inputBlock,
                    ByteBufCodecs.registry(Registries.BLOCK),
                    LaserIrradiationRecipe::outputBlock,
                    ByteBufCodecs.VAR_LONG,
                    LaserIrradiationRecipe::requiredEnergy,
                    LaserIrradiationRecipe::new);

    public BlockState outputState() {
        return this.outputBlock.defaultBlockState();
    }

    public boolean matches(BlockState state) {
        return state.is(this.inputBlock);
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<LaserIrradiationRecipe> getSerializer() {
        return ClayiumRecipeSerializers.LASER_IRRADIATION_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<LaserIrradiationRecipe> getType() {
        return ClayiumRecipeTypes.LASER_IRRADIATION_RECIPE_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new LaserIrradiationRecipeDisplay(this.inputBlock, this.outputBlock, this.requiredEnergy));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ClayiumRecipeBookCategories.CLAYIUM.get();
    }
}
