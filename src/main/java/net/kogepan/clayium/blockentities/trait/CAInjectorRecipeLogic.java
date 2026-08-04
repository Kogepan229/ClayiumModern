package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;

import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import org.jetbrains.annotations.NotNull;

/** Applies the CA Injector's tier-dependent resonance duration multiplier. */
public class CAInjectorRecipeLogic extends EnergyRecipeLogic {

    private final ResonanceManager resonanceManager;

    public CAInjectorRecipeLogic(@NotNull ClayContainerBlockEntity blockEntity,
                                 RecipeType<MachineRecipe> recipeType,
                                 @NotNull ClayEnergyHolder energyHolder,
                                 @NotNull ResonanceManager resonanceManager) {
        super(blockEntity, recipeType, energyHolder);
        this.resonanceManager = resonanceManager;
    }

    @Override
    protected long getRecipeDuration(RecipeHolder<?> recipeHolder) {
        long baseDuration = super.getRecipeDuration(recipeHolder);
        double exponent = switch (this.blockEntity.tier) {
            case 9 -> 0.2D;
            case 10 -> 0.9D;
            case 11 -> 3.0D;
            default -> 1.0D;
        };
        double resonance = this.resonanceManager.getResonance();
        if (resonance <= 1.0D) {
            return Math.max(1L, baseDuration);
        }
        double duration = baseDuration * Math.pow(resonance, -exponent);
        return Math.max(1L, (long) Math.floor(duration));
    }
}
