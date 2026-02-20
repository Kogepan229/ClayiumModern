package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.utils.TierMultipliers;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Recipe logic that applies the "base" (tierGeneric) tier multipliers to CE per tick and
 * duration. Used by Clay Condenser and Grinder.
 */
public class BaseMultiplierRecipeLogic extends EnergyRecipeLogic {

    public BaseMultiplierRecipeLogic(@NotNull ClayContainerBlockEntity blockEntity,
                                     RecipeType<MachineRecipe> recipeType,
                                     @NotNull ClayEnergyHolder energyHolder) {
        super(blockEntity, recipeType, energyHolder);
    }

    @Override
    protected RecipeHolder<?> getMatchedRecipe(Level level, List<ItemStack> inventoryStacks) {
        return super.getMatchedRecipe(level, inventoryStacks);
    }

    @Override
    protected long getRecipeCEPerTick(RecipeHolder<?> recipeHolder) {
        long base = super.getRecipeCEPerTick(recipeHolder);
        return (long) (base * TierMultipliers.getBaseEnergyMultiplier(blockEntity.tier));
    }

    @Override
    protected long getRecipeDuration(RecipeHolder<?> recipeHolder) {
        long base = super.getRecipeDuration(recipeHolder);
        return (long) (base * TierMultipliers.getBaseTimeMultiplier(blockEntity.tier));
    }
}
