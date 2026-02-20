package net.kogepan.clayium.recipes.recipes;

import net.kogepan.clayium.recipes.ClayiumRecipeSerializers;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

/**
 * Recipe for laser block irradiation. When a block (without a laser acceptor) is irradiated
 * and matches the input block, and energy/tick conditions are met, the block is replaced by
 * the output block. Not used in a crafting grid; matching is done via
 * {@link #matches(BlockState, long)}.
 */
public record LaserIrradiationRecipe(
                                     @NotNull Block inputBlock,
                                     @NotNull Block outputBlock,
                                     long energyMin,
                                     long energyMax,
                                     long requiredEnergy)
        implements Recipe<SingleRecipeInput> {

    /** Output block state (default state of output block) for placement. */
    @NotNull
    public BlockState outputState() {
        return outputBlock.defaultBlockState();
    }

    /**
     * Returns true if the given block state and tick energy match this recipe's criteria.
     * Does not check accumulated energy or tick count; the handler does that.
     */
    public boolean matches(@NotNull BlockState state, long tickEnergy) {
        return state.is(inputBlock) && tickEnergy >= energyMin && tickEnergy <= energyMax;
    }

    @Override
    public boolean matches(@NotNull SingleRecipeInput input, @NotNull Level level) {
        return false;
    }

    @Override
    @NotNull
    public ItemStack assemble(@NotNull SingleRecipeInput input, @NotNull HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    @NotNull
    public ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        return new ItemStack(outputBlock);
    }

    @Override
    @NotNull
    public RecipeSerializer<?> getSerializer() {
        return ClayiumRecipeSerializers.LASER_IRRADIATION_RECIPE_SERIALIZER.get();
    }

    @Override
    @NotNull
    public RecipeType<?> getType() {
        return ClayiumRecipeTypes.LASER_IRRADIATION_RECIPE_TYPE.get();
    }
}
