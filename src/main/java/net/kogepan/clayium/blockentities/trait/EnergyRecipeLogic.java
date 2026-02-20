package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnergyRecipeLogic extends AbstractRecipeLogic {

    @NotNull
    private final ClayEnergyHolder energyHolder;
    protected final RecipeType<MachineRecipe> recipeType;

    public EnergyRecipeLogic(@NotNull ClayContainerBlockEntity blockEntity, RecipeType<MachineRecipe> recipeType,
                             @NotNull ClayEnergyHolder energyHolder) {
        super(blockEntity, recipeType);
        this.energyHolder = energyHolder;
        this.recipeType = recipeType;
    }

    @Override
    protected boolean drawEnergy(long amount, boolean simulate) {
        return this.energyHolder.drawEnergy(amount, simulate);
    }

    @Override
    protected RecipeHolder<?> getMatchedRecipe(Level level, List<ItemStack> inventoryStacks) {
        var recipeHolders = level.getRecipeManager()
                .getAllRecipesFor(this.recipeType);

        for (RecipeHolder<MachineRecipe> holder : recipeHolders) {
            MachineRecipe recipe = holder.value();
            if (recipe.recipeTier() <= this.blockEntity.tier && recipe.matchesItems(inventoryStacks)) {
                return holder;
            }
        }

        return null;
    }
}
