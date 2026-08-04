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
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
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

        this.smelting(
                ClayiumItems.RAW_CLAY_ROLLING_PIN.get(),
                ClayiumItems.CLAY_ROLLING_PIN.get(),
                "clay_rolling_pin_from_smelting");
        this.smelting(
                ClayiumItems.RAW_CLAY_SLICER.get(),
                ClayiumItems.CLAY_SLICER.get(),
                "clay_slicer_from_smelting");
        this.smelting(
                ClayiumItems.RAW_CLAY_SPATULA.get(),
                ClayiumItems.CLAY_SPATULA.get(),
                "clay_spatula_from_smelting");

        this.shaped(RecipeCategory.MISC, ClayiumItems.CLAY_CIRCUIT.get())
                .pattern("DGD")
                .pattern("RBR")
                .pattern("DGD")
                .define('D', ClayiumItems.DENSE_CLAY_STICK.get())
                .define('G', ClayiumItems.DENSE_CLAY_GEAR.get())
                .define('R', ClayiumItems.DENSE_CLAY_RING.get())
                .define('B', ClayiumItems.CLAY_CIRCUIT_BOARD.get())
                .unlockedBy("has_clay_circuit_board", this.has(ClayiumItems.CLAY_CIRCUIT_BOARD.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Clayium.id("clay_circuit")));
        this.shaped(RecipeCategory.MISC, ClayiumItems.SIMPLE_CIRCUIT.get())
                .pattern("EEE")
                .pattern("EBE")
                .pattern("EEE")
                .define('E', ClayiumItems.ENERGIZED_CLAY_DUST.get())
                .define('B', ClayiumItems.CLAY_CIRCUIT_BOARD.get())
                .unlockedBy("has_clay_circuit_board", this.has(ClayiumItems.CLAY_CIRCUIT_BOARD.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Clayium.id("simple_circuit")));

        this.shaped(RecipeCategory.TOOLS, ClayiumItems.RAW_CLAY_ROLLING_PIN.get())
                .pattern("SCS")
                .define('S', ClayiumItems.SHORT_CLAY_STICK.get())
                .define('C', ClayiumItems.CLAY_CYLINDER.get())
                .unlockedBy("has_clay_cylinder", this.has(ClayiumItems.CLAY_CYLINDER.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Clayium.id("raw_clay_rolling_pin")));
        this.shaped(RecipeCategory.TOOLS, ClayiumItems.RAW_CLAY_SPATULA.get())
                .pattern("SB")
                .define('S', ClayiumItems.SHORT_CLAY_STICK.get())
                .define('B', ClayiumItems.CLAY_BLADE.get())
                .unlockedBy("has_clay_blade", this.has(ClayiumItems.CLAY_BLADE.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Clayium.id("raw_clay_spatula")));
        this.shaped(RecipeCategory.TOOLS, ClayiumItems.CLAY_SHOVEL.get())
                .pattern("P")
                .pattern("S")
                .pattern("S")
                .define('P', ClayiumItems.CLAY_PLATE.get())
                .define('S', ClayiumItems.CLAY_STICK.get())
                .unlockedBy("has_clay_plate", this.has(ClayiumItems.CLAY_PLATE.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Clayium.id("clay_shovel")));
        this.shaped(RecipeCategory.TOOLS, ClayiumItems.CLAY_PICKAXE.get())
                .pattern("PPP")
                .pattern(" S ")
                .pattern(" S ")
                .define('P', ClayiumItems.DENSE_CLAY_PLATE.get())
                .define('S', ClayiumItems.DENSE_CLAY_STICK.get())
                .unlockedBy("has_dense_clay_plate", this.has(ClayiumItems.DENSE_CLAY_PLATE.get()))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Clayium.id("clay_pickaxe")));
    }

    private void smelting(ItemLike ingredient, ItemLike result, String path) {
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(ingredient), RecipeCategory.TOOLS, CookingBookCategory.MISC, result, 0.1F, 200)
                .unlockedBy("has_" + path, this.has(ingredient))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Clayium.id(path)));
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
