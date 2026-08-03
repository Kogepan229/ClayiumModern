package net.kogepan.clayium.recipes.recipes;

import net.kogepan.clayium.recipes.ClayiumRecipeSerializers;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

/** Switches Auto Eat modes while preserving the source stack's data components. */
public class AutoEatGadgetToggleRecipe extends CustomRecipe {

    public AutoEatGadgetToggleRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return findSource(input) != null;
    }

    @Override
    @NotNull
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack source = findSource(input);
        if (source == null) {
            return ItemStack.EMPTY;
        }
        ItemStack result = source.is(ClayiumItems.GADGET_AUTO_EAT) ? ClayiumItems.GADGET_AUTO_EAT_ECONOMICAL.toStack() :
                ClayiumItems.GADGET_AUTO_EAT.toStack();
        result.applyComponents(source.getComponentsPatch());
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    @NotNull
    public RecipeSerializer<?> getSerializer() {
        return ClayiumRecipeSerializers.AUTO_EAT_GADGET_TOGGLE_RECIPE_SERIALIZER.get();
    }

    private static ItemStack findSource(CraftingInput input) {
        if (input.ingredientCount() != 1) {
            return null;
        }
        for (ItemStack stack : input.items()) {
            if (stack.is(ClayiumItems.GADGET_AUTO_EAT) || stack.is(ClayiumItems.GADGET_AUTO_EAT_ECONOMICAL)) {
                return stack;
            }
            if (!stack.isEmpty()) {
                return null;
            }
        }
        return null;
    }
}
