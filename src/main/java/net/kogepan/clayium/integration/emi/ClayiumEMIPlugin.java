package net.kogepan.clayium.integration.emi;

import net.kogepan.clayium.integration.XEIMachineRecipeCategory;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;

import java.util.Comparator;
import java.util.function.Function;

@EmiEntrypoint
public class ClayiumEMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
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
                                return recipe.duration() * recipe.cePerTick();
                            }))
                    .map((holder) -> new EMIMachineRecipe(emiCategory, holder))
                    .forEach(registry::addRecipe);
        }
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
