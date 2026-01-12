package net.kogepan.clayium.recipes.recipes;

import net.kogepan.clayium.recipes.ClayiumRecipeSerializers;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

public class ClayWorkTableRecipe implements Recipe<SingleRecipeInput> {

    private final ItemIngredientStack ingredient;
    private final ItemStack result;
    private final ItemStack byproduct;
    private final int button;
    private final int cost;

    public ClayWorkTableRecipe(@NotNull ItemIngredientStack ingredient, @NotNull ItemStack result,
                               @NotNull ItemStack byproduct, int button,
                               int cost) {
        this.ingredient = ingredient;
        this.result = result;
        this.byproduct = byproduct;
        this.button = button;
        this.cost = cost;
    }

    @NotNull
    public ItemIngredientStack getIngredient() {
        return this.ingredient;
    }

    @NotNull
    public ItemStack getResult() {
        return this.result;
    }

    @NotNull
    public ItemStack getByproduct() {
        return this.byproduct;
    }

    public int getButton() {
        return this.button;
    }

    public int getCost() {
        return this.cost;
    }

    @Override
    public boolean matches(@NotNull SingleRecipeInput input, @NotNull Level level) {
        return false;
    }

    @Override
    @NotNull
    public ItemStack assemble(@NotNull SingleRecipeInput input, @NotNull HolderLookup.Provider provider) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    @NotNull
    public ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        return this.result;
    }

    @Override
    @NotNull
    public RecipeSerializer<?> getSerializer() {
        return ClayiumRecipeSerializers.CLAY_WORK_TABLE_RECIPE_SERIALIZER.get();
    }

    @Override
    @NotNull
    public RecipeType<?> getType() {
        return ClayiumRecipeTypes.CLAY_WORK_TABLE_RECIPE_TYPE.get();
    }
}
