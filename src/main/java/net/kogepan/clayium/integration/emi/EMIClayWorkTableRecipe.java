package net.kogepan.clayium.integration.emi;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.recipes.ClayWorkTableRecipe;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import com.lowdragmc.lowdraglib2.integration.xei.emi.ModularUIEMIRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import org.jetbrains.annotations.Nullable;

public class EMIClayWorkTableRecipe extends ModularUIEMIRecipe {

    public static class EMIClayWorkTableRecipeCategory extends EmiRecipeCategory {

        public EMIClayWorkTableRecipeCategory() {
            super(Clayium.id("clay_work_table"), EmiStack.of(ClayiumBlocks.CLAY_WORK_TABLE));
        }
    }

    public static EMIClayWorkTableRecipeCategory CATEGORY = new EMIClayWorkTableRecipeCategory();

    private final RecipeHolder<ClayWorkTableRecipe> holder;

    public EMIClayWorkTableRecipe(RecipeHolder<ClayWorkTableRecipe> holder) {
        super(recipe -> holder.value().createModularUI());

        this.holder = holder;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return holder.id();
    }

    @Override
    public int getDisplayWidth() {
        return ClayWorkTableRecipe.WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return ClayWorkTableRecipe.HEIGHT;
    }
}
