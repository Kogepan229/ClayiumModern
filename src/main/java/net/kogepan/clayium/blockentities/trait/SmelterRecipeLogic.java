package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Recipe logic for the Smelter machine. Runs Smelter-specific recipes first, then falls back to
 * vanilla furnace (smelting) recipes. Applies tier-based energy and time multipliers.
 */
public class SmelterRecipeLogic extends EnergyRecipeLogic {

    /**
     * CE consumed per tick for vanilla smelting recipes (base value before tier multiplier).
     */
    private static final long VANILLA_SMELTING_CE_PER_TICK = 4L;

    /**
     * Energy multiplier per tier (tier 4–9). Higher tier consumes more CE per tick.
     */
    private static final double[] TIER_ENERGY_MULTIPLIER = { 1.0, 14.0, 200.0, 2800.0, 40000.0, 560000.0 };

    /**
     * Time multiplier per tier (tier 4–9). Higher tier has shorter craft time.
     */
    private static final double[] TIER_TIME_MULTIPLIER = { 2.0, 0.5, 0.125, 0.03, 0.01, 0.0025 };

    private static double getEnergyMultiplier(int tier) {
        int idx = tier - 4;
        if (idx >= 0 && idx < TIER_ENERGY_MULTIPLIER.length) {
            return TIER_ENERGY_MULTIPLIER[idx];
        }
        return 1.0;
    }

    private static double getTimeMultiplier(int tier) {
        int idx = tier - 4;
        if (idx >= 0 && idx < TIER_TIME_MULTIPLIER.length) {
            return TIER_TIME_MULTIPLIER[idx];
        }
        return 1.0;
    }

    public SmelterRecipeLogic(@NotNull ClayContainerBlockEntity blockEntity,
                              RecipeType<MachineRecipe> recipeType,
                              @NotNull ClayEnergyHolder energyHolder) {
        super(blockEntity, recipeType, energyHolder);
    }

    @Override
    protected RecipeHolder<?> getMatchedRecipe(Level level, List<ItemStack> inventoryStacks) {
        RecipeHolder<?> matched = super.getMatchedRecipe(level, inventoryStacks);
        if (matched != null) {
            return matched;
        }

        for (ItemStack stack : inventoryStacks) {
            if (stack.isEmpty()) continue;
            var holder = level.getRecipeManager()
                    .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level);
            if (holder.isPresent()) {
                return holder.get();
            }
        }
        return null;
    }

    @Override
    protected long getRecipeCEPerTick(RecipeHolder<?> recipeHolder) {
        long base;
        if (recipeHolder.value() instanceof AbstractCookingRecipe) {
            base = VANILLA_SMELTING_CE_PER_TICK;
        } else {
            base = super.getRecipeCEPerTick(recipeHolder);
        }
        return (long) (base * getEnergyMultiplier(blockEntity.tier));
    }

    @Override
    protected long getRecipeDuration(RecipeHolder<?> recipeHolder) {
        long base;
        if (recipeHolder.value() instanceof AbstractCookingRecipe recipe) {
            base = recipe.getCookingTime();
        } else {
            base = super.getRecipeDuration(recipeHolder);
        }
        return (long) (base * getTimeMultiplier(blockEntity.tier));
    }

    @Override
    protected List<ItemIngredientStack> getRecipeInputs(RecipeHolder<?> recipeHolder) {
        if (recipeHolder.value() instanceof AbstractCookingRecipe recipe) {
            return List.of(new ItemIngredientStack(recipe.getIngredients().getFirst(), 1));
        }
        return super.getRecipeInputs(recipeHolder);
    }

    @Override
    protected List<ItemStack> getRecipeOutputs(RecipeHolder<?> recipeHolder) {
        if (recipeHolder.value() instanceof AbstractCookingRecipe recipe) {
            Level level = blockEntity.getLevel();
            if (level != null) {
                return List.of(recipe.getResultItem(level.registryAccess()));
            }
        }
        return super.getRecipeOutputs(recipeHolder);
    }

    @Override
    protected List<ItemStack> getCopiedRecipeOutputs(RecipeHolder<?> recipeHolder) {
        if (recipeHolder.value() instanceof AbstractCookingRecipe recipe) {
            Level level = blockEntity.getLevel();
            if (level != null) {
                return List.of(recipe.getResultItem(level.registryAccess()).copy());
            }
        }
        return super.getCopiedRecipeOutputs(recipeHolder);
    }
}
