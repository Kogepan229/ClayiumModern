package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ClayiumRecipeProvider extends RecipeProvider {

    private ClayiumRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        MachineRecipe clayPlate = new MachineRecipe(
                ClayiumRecipeTypes.BENDING_MACHINE_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(Ingredient.of(Blocks.CLAY), 1)),
                List.of(new ItemStackTemplate(ClayiumItems.CLAY_PLATE.get())),
                1L,
                CEUtils.TEN_MICRO_CE,
                0);
        ResourceKey<Recipe<?>> id = ResourceKey.create(
                Registries.RECIPE,
                Clayium.id("bending_machine/clay_plate"));
        this.output.accept(id, clayPlate, null);
    }

    public static final class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ClayiumRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Clayium Recipes";
        }
    }
}
