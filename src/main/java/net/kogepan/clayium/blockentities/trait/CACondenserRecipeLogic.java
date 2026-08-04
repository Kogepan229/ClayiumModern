package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Applies CA Condenser tier multipliers and resonance-scaled output counts. */
public class CACondenserRecipeLogic extends EnergyRecipeLogic {

    private final ResonanceManager resonanceManager;

    public CACondenserRecipeLogic(@NotNull ClayContainerBlockEntity blockEntity,
                                  RecipeType<MachineRecipe> recipeType,
                                  @NotNull ClayEnergyHolder energyHolder,
                                  @NotNull ResonanceManager resonanceManager) {
        super(blockEntity, recipeType, energyHolder);
        this.resonanceManager = resonanceManager;
    }

    @Override
    protected long getRecipeCEPerTick(RecipeHolder<?> recipeHolder) {
        long multiplier = switch (this.blockEntity.tier) {
            case 10 -> 10L;
            case 11 -> 100L;
            default -> 1L;
        };
        long base = super.getRecipeCEPerTick(recipeHolder);
        return base > Long.MAX_VALUE / multiplier ? Long.MAX_VALUE : base * multiplier;
    }

    @Override
    protected long getRecipeDuration(RecipeHolder<?> recipeHolder) {
        long base = super.getRecipeDuration(recipeHolder);
        double multiplier = switch (this.blockEntity.tier) {
            case 10 -> 0.1D;
            case 11 -> 0.01D;
            default -> 1.0D;
        };
        return Math.max(1L, (long) (base * multiplier));
    }

    @Override
    protected List<ItemStack> getCopiedRecipeOutputs(RecipeHolder<?> recipeHolder) {
        double multiplier = Math.log(this.resonanceManager.getResonance()) + 1.0D;
        return super.getCopiedRecipeOutputs(recipeHolder).stream().map(stack -> {
            int count = (int) Math.min(stack.getMaxStackSize(), Math.floor(stack.getCount() * multiplier));
            stack.setCount(Math.max(1, count));
            return stack;
        }).toList();
    }
}
