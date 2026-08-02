package net.kogepan.clayium.integration.emi;

import net.kogepan.clayium.gui.menu.ClayCraftingBoardMenu;

import net.minecraft.world.inventory.Slot;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClayCraftingBoardEmiRecipeHandler implements StandardRecipeHandler<ClayCraftingBoardMenu> {

    @Override
    @NotNull
    public List<Slot> getInputSources(@NotNull ClayCraftingBoardMenu menu) {
        return menu.getInputSources();
    }

    @Override
    @NotNull
    public List<Slot> getCraftingSlots(@NotNull ClayCraftingBoardMenu menu) {
        return menu.getCraftingSlots();
    }

    @Override
    public Slot getOutputSlot(@NotNull ClayCraftingBoardMenu menu) {
        return menu.getOutputSlot();
    }

    @Override
    public boolean supportsRecipe(@NotNull EmiRecipe recipe) {
        return recipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING && recipe.supportsRecipeTree();
    }
}
