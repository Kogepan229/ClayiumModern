package net.kogepan.clayium.recipes.recipes;

import net.kogepan.clayium.recipes.ClayiumRecipeSerializers;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

/**
 * Recipe for the Quartz Crucible. Defines input item (dropped into crucible), catalyst item
 * (dropped to collect result), output per unit, and duration in ticks per unit.
 */
public record QuartzCrucibleRecipe(
                                   @NotNull Ingredient input,
                                   @NotNull Ingredient catalyst,
                                   @NotNull ItemStack result,
                                   int duration)
        implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(@NotNull SingleRecipeInput input, @NotNull Level level) {
        return false;
    }

    @Override
    @NotNull
    public ItemStack assemble(@NotNull SingleRecipeInput input, @NotNull HolderLookup.Provider provider) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    @NotNull
    public ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        return result;
    }

    /**
     * Returns the output stack for the given quantity (e.g. quantity ingots processed).
     */
    @NotNull
    public ItemStack getResultForQuantity(int quantity) {
        ItemStack out = result.copy();
        out.setCount(result.getCount() * quantity);
        return out;
    }

    @Override
    @NotNull
    public RecipeSerializer<?> getSerializer() {
        return ClayiumRecipeSerializers.QUARTZ_CRUCIBLE_RECIPE_SERIALIZER.get();
    }

    @Override
    @NotNull
    public RecipeType<?> getType() {
        return ClayiumRecipeTypes.QUARTZ_CRUCIBLE_RECIPE_TYPE.get();
    }
}
