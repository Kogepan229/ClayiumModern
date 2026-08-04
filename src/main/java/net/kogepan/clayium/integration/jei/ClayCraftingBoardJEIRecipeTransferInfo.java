package net.kogepan.clayium.integration.jei;

import net.kogepan.clayium.gui.menu.ClayCraftingBoardMenu;
import net.kogepan.clayium.registries.ClayiumMenuTypes;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class ClayCraftingBoardJEIRecipeTransferInfo
                                                          implements
                                                          IRecipeTransferInfo<ClayCraftingBoardMenu, RecipeHolder<CraftingRecipe>> {

    @Override
    public Class<? extends ClayCraftingBoardMenu> getContainerClass() {
        return ClayCraftingBoardMenu.class;
    }

    @Override
    public Optional<MenuType<ClayCraftingBoardMenu>> getMenuType() {
        return Optional.of(ClayiumMenuTypes.CLAY_CRAFTING_BOARD_UI.get());
    }

    @Override
    public RecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
        return RecipeTypes.CRAFTING;
    }

    @Override
    public boolean canHandle(ClayCraftingBoardMenu container, RecipeHolder<CraftingRecipe> recipe) {
        return recipe.value().canCraftInDimensions(3, 3);
    }

    @Override
    public List<Slot> getRecipeSlots(ClayCraftingBoardMenu container, RecipeHolder<CraftingRecipe> recipe) {
        return container.getCraftingSlots();
    }

    @Override
    public List<Slot> getInventorySlots(ClayCraftingBoardMenu container, RecipeHolder<CraftingRecipe> recipe) {
        Set<Slot> recipeSlots = Collections.newSetFromMap(new IdentityHashMap<>());
        recipeSlots.addAll(container.getCraftingSlots());
        return container.getInputSources().stream()
                .filter(slot -> !recipeSlots.contains(slot))
                .toList();
    }
}
