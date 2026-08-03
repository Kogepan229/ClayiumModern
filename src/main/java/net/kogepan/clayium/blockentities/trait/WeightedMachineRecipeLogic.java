package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.WeightedMachineRecipe;
import net.kogepan.clayium.utils.TransferUtils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class WeightedMachineRecipeLogic extends AbstractRecipeLogic {

    @NotNull
    private final ClayEnergyHolder energyHolder;
    private final RecipeType<WeightedMachineRecipe> weightedRecipeType;

    public WeightedMachineRecipeLogic(@NotNull ClayContainerBlockEntity blockEntity,
                                      @NotNull RecipeType<WeightedMachineRecipe> recipeType,
                                      @NotNull ClayEnergyHolder energyHolder) {
        super(blockEntity, recipeType);
        this.energyHolder = energyHolder;
        this.weightedRecipeType = recipeType;
    }

    @Override
    protected boolean drawEnergy(long amount, boolean simulate) {
        return this.energyHolder.drawEnergy(amount, simulate);
    }

    @Override
    protected RecipeHolder<?> getMatchedRecipe(Level level, List<ItemStack> inventoryStacks) {
        for (RecipeHolder<WeightedMachineRecipe> holder : level.getRecipeManager()
                .getAllRecipesFor(this.weightedRecipeType)) {
            WeightedMachineRecipe recipe = holder.value();
            if (recipe.recipeTier() <= this.blockEntity.tier && recipe.matchesItems(inventoryStacks)) {
                return holder;
            }
        }
        return null;
    }

    @Override
    protected long getRecipeCEPerTick(RecipeHolder<?> recipeHolder) {
        return getRecipe(recipeHolder).cePerTick();
    }

    @Override
    protected long getRecipeDuration(RecipeHolder<?> recipeHolder) {
        return getRecipe(recipeHolder).adjustedDuration();
    }

    @Override
    protected List<ItemIngredientStack> getRecipeInputs(RecipeHolder<?> recipeHolder) {
        return getRecipe(recipeHolder).inputs();
    }

    @Override
    protected List<ItemStack> getRecipeOutputs(RecipeHolder<?> recipeHolder) {
        return getRecipe(recipeHolder).possibleOutputs();
    }

    @Override
    protected List<ItemStack> getCopiedRecipeOutputs(RecipeHolder<?> recipeHolder) {
        Level level = Objects.requireNonNull(this.blockEntity.getLevel());
        return List.of(getRecipe(recipeHolder).rollOutput(level.getRandom()));
    }

    @Override
    protected boolean hasEnoughOutputSpace(RecipeHolder<?> recipeHolder) {
        for (ItemStack possibleOutput : getRecipeOutputs(recipeHolder)) {
            if (!TransferUtils.simulateInsertItemsToHandler(this.blockEntity.getOutputInventory(),
                    List.of(possibleOutput))) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean canContinueProcessing(RecipeHolder<?> recipeHolder) {
        return hasEnoughOutputSpace(recipeHolder);
    }

    @Override
    protected long getInitialProgress() {
        return 0;
    }

    private static WeightedMachineRecipe getRecipe(RecipeHolder<?> recipeHolder) {
        if (recipeHolder.value() instanceof WeightedMachineRecipe recipe) {
            return recipe;
        }
        throw new IllegalStateException("Expected a weighted machine recipe");
    }
}
