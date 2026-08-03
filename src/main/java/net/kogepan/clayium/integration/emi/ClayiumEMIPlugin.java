package net.kogepan.clayium.integration.emi;

import net.kogepan.clayium.integration.XEIChemicalMetalSeparatorRecipeCategory;
import net.kogepan.clayium.integration.XEIMachineRecipeCategory;
import net.kogepan.clayium.integration.XEISaltExtractorRecipeCategory;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumMenuTypes;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;

import java.util.Comparator;
import java.util.function.Function;

@EmiEntrypoint
public class ClayiumEMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING,
                EmiStack.of(ClayiumBlocks.CLAY_CRAFTING_BOARD));
        registry.addRecipeHandler(ClayiumMenuTypes.CLAY_CRAFTING_BOARD_UI.get(),
                new ClayCraftingBoardEmiRecipeHandler());

        registry.addCategory(EMIClayWorkTableRecipe.CATEGORY);
        registry.addWorkstation(EMIClayWorkTableRecipe.CATEGORY, EmiStack.of(ClayiumBlocks.CLAY_WORK_TABLE));
        registry.getRecipeManager().getAllRecipesFor(ClayiumRecipeTypes.CLAY_WORK_TABLE_RECIPE_TYPE.get())
                .stream()
                .sorted(Comparator.comparingInt(r -> r.value().button()))
                .map(EMIClayWorkTableRecipe::new)
                .forEach(registry::addRecipe);

        for (XEIMachineRecipeCategory category : XEIMachineRecipeCategory.getCategories()) {
            EMIMachineRecipe.EMIMachineRecipeCategory emiCategory = new EMIMachineRecipe.EMIMachineRecipeCategory(
                    category);
            registry.addCategory(emiCategory);
            for (var block : category.getWorkstations()) {
                registry.addWorkstation(emiCategory, EmiStack.of(block));
            }
            registry.getRecipeManager().getAllRecipesFor(category.getRecipeType())
                    .stream()
                    .sorted(Comparator
                            .<RecipeHolder<MachineRecipe>>comparingInt(r -> r.value().recipeTier())
                            .thenComparingLong(r -> {
                                MachineRecipe recipe = r.value();
                                return recipe.adjustedTotalCE();
                            }))
                    .map((holder) -> new EMIMachineRecipe(emiCategory, holder))
                    .forEach(registry::addRecipe);
        }

        registry.addCategory(EMISaltExtractorRecipe.CATEGORY);
        for (var recipe : XEISaltExtractorRecipeCategory.getRecipes()) {
            registry.addWorkstation(EMISaltExtractorRecipe.CATEGORY, EmiStack.of(recipe.machine()));
            registry.addRecipe(new EMISaltExtractorRecipe(recipe));
        }

        registry.addCategory(EMIChemicalMetalSeparatorRecipe.CATEGORY);
        registry.addWorkstation(EMIChemicalMetalSeparatorRecipe.CATEGORY,
                EmiStack.of(ClayiumBlocks.CHEMICAL_METAL_SEPARATOR));
        XEIChemicalMetalSeparatorRecipeCategory.getRecipes(registry.getRecipeManager()).stream()
                .map(EMIChemicalMetalSeparatorRecipe::new)
                .forEach(registry::addRecipe);
    }

    private static <C extends RecipeInput, T extends Recipe<C>> void adaptRecipeType(EmiRegistry registry,
                                                                                     RecipeType<T> recipeType,
                                                                                     Function<RecipeHolder<T>, ? extends EmiRecipe> adapter) {
        registry.getRecipeManager().getAllRecipesFor(recipeType)
                .stream()
                .map(adapter)
                .forEach(registry::addRecipe);
    }
}
