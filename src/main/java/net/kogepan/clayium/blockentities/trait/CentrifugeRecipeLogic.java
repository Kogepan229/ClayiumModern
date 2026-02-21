package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CentrifugeRecipeLogic extends BaseMultiplierRecipeLogic {

    public CentrifugeRecipeLogic(@NotNull ClayContainerBlockEntity blockEntity, RecipeType<MachineRecipe> recipeType,
                                 @NotNull ClayEnergyHolder energyHolder) {
        super(blockEntity, recipeType, energyHolder);
    }

    @Override
    protected List<ItemStack> getRecipeOutputs(RecipeHolder<?> recipeHolder) {
        int outputNum = this.blockEntity.tier - 2;
        List<ItemStack> recipeOutputs = super.getRecipeOutputs(recipeHolder);

        if (recipeOutputs.size() <= outputNum) {
            return recipeOutputs;
        }

        List<ItemStack> shrunkOutputs = new ArrayList<>(outputNum);
        for (int i = 0; i < outputNum; i++) {
            shrunkOutputs.add(recipeOutputs.get(i));
        }
        return shrunkOutputs;
    }

    @Override
    protected List<ItemStack> getCopiedRecipeOutputs(RecipeHolder<?> recipeHolder) {
        int outputNum = this.blockEntity.tier - 2;
        List<ItemStack> recipeOutputs = super.getCopiedRecipeOutputs(recipeHolder);

        if (recipeOutputs.size() <= outputNum) {
            return recipeOutputs;
        }

        List<ItemStack> shrunkOutputs = new ArrayList<>(outputNum);
        for (int i = 0; i < outputNum; i++) {
            shrunkOutputs.add(recipeOutputs.get(i));
        }
        return shrunkOutputs;
    }
}
