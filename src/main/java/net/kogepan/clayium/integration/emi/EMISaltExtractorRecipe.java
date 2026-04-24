package net.kogepan.clayium.integration.emi;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.integration.XEISaltExtractorRecipeCategory;

import net.minecraft.resources.ResourceLocation;

import com.lowdragmc.lowdraglib2.integration.xei.emi.ModularUIEMIRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;

public class EMISaltExtractorRecipe extends ModularUIEMIRecipe {

    public static class EMISaltExtractorRecipeCategory extends EmiRecipeCategory {

        public EMISaltExtractorRecipeCategory() {
            super(Clayium.id("salt_extractor"), EmiStack.of(XEISaltExtractorRecipeCategory.getIcon()));
        }
    }

    public static final EMISaltExtractorRecipeCategory CATEGORY = new EMISaltExtractorRecipeCategory();
    private final XEISaltExtractorRecipeCategory.XEISaltExtractorRecipe recipe;

    public EMISaltExtractorRecipe(XEISaltExtractorRecipeCategory.XEISaltExtractorRecipe recipe) {
        super(r -> recipe.createModularUI());
        this.recipe = recipe;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return CATEGORY;
    }

    @Override
    public ResourceLocation getId() {
        return Clayium.id("/salt_extractor/" + this.recipe.tier());
    }

    @Override
    public int getDisplayWidth() {
        return XEISaltExtractorRecipeCategory.XEISaltExtractorRecipe.WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return XEISaltExtractorRecipeCategory.XEISaltExtractorRecipe.HEIGHT;
    }
}
