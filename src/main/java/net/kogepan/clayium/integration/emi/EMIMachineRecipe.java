package net.kogepan.clayium.integration.emi;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.integration.XEIMachineRecipeCategory;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import com.lowdragmc.lowdraglib2.integration.xei.emi.ModularUIEMIRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import org.jetbrains.annotations.Nullable;

public class EMIMachineRecipe extends ModularUIEMIRecipe {

    public static class EMIMachineRecipeCategory extends EmiRecipeCategory {

        public EMIMachineRecipeCategory(XEIMachineRecipeCategory category) {
            super(Clayium.id(category.getId().getPath()), EmiStack.of(category.getIcon()));
        }
    }

    private final RecipeHolder<MachineRecipe> holder;
    private final EmiRecipeCategory category;

    public EMIMachineRecipe(EmiRecipeCategory category, RecipeHolder<MachineRecipe> holder) {
        super(recipe -> holder.value().createModularUI());

        this.holder = holder;
        this.category = category;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return this.category;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return holder.id();
    }

    @Override
    public int getDisplayWidth() {
        return MachineRecipe.WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return MachineRecipe.HEIGHT;
    }
}
