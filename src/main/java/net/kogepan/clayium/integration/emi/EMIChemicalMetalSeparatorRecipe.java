package net.kogepan.clayium.integration.emi;

import net.kogepan.clayium.integration.XEIChemicalMetalSeparatorRecipeCategory;

import net.minecraft.resources.ResourceLocation;

import com.lowdragmc.lowdraglib2.integration.xei.emi.ModularUIEMIRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;

public class EMIChemicalMetalSeparatorRecipe extends ModularUIEMIRecipe {

    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(
            XEIChemicalMetalSeparatorRecipeCategory.getId(),
            EmiStack.of(XEIChemicalMetalSeparatorRecipeCategory.getIcon()));

    private final XEIChemicalMetalSeparatorRecipeCategory.XEIChemicalMetalSeparatorRecipe recipe;

    public EMIChemicalMetalSeparatorRecipe(
                                           XEIChemicalMetalSeparatorRecipeCategory.XEIChemicalMetalSeparatorRecipe recipe) {
        super(ignored -> recipe.createModularUI());
        this.recipe = recipe;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return CATEGORY;
    }

    @Override
    public ResourceLocation getId() {
        return this.recipe.getId();
    }

    @Override
    public int getDisplayWidth() {
        return XEIChemicalMetalSeparatorRecipeCategory.XEIChemicalMetalSeparatorRecipe.WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return this.recipe.getDisplayHeight();
    }
}
