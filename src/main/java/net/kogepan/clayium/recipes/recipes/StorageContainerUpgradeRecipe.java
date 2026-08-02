package net.kogepan.clayium.recipes.recipes;

import net.kogepan.clayium.blockentities.StorageContainerBlockEntity;
import net.kogepan.clayium.items.blockitem.StorageContainerBlockItem;
import net.kogepan.clayium.recipes.ClayiumRecipeSerializers;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class StorageContainerUpgradeRecipe extends CustomRecipe {

    public StorageContainerUpgradeRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return findStorageContainer(input) != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack storageContainer = findStorageContainer(input);
        if (storageContainer == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = storageContainer.copyWithCount(1);
        StorageContainerBlockItem.setCapacity(result, Integer.MAX_VALUE);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ClayiumRecipeSerializers.STORAGE_CONTAINER_UPGRADE_RECIPE_SERIALIZER.get();
    }

    private static ItemStack findStorageContainer(CraftingInput input) {
        if (input.ingredientCount() != 2) {
            return null;
        }

        ItemStack storageContainer = null;
        boolean hasClayCore = false;
        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(ClayiumBlocks.STORAGE_CONTAINER.asItem())) {
                if (storageContainer != null ||
                        StorageContainerBlockItem.getCapacity(stack) != StorageContainerBlockEntity.DEFAULT_CAPACITY) {
                    return null;
                }
                storageContainer = stack;
            } else if (stack.is(ClayiumItems.CLAY_CORE)) {
                if (hasClayCore) {
                    return null;
                }
                hasClayCore = true;
            } else {
                return null;
            }
        }

        return storageContainer != null && hasClayCore ? storageContainer : null;
    }
}
