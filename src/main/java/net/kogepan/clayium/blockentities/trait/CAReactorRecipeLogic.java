package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.machine.CAReactorBlockEntity;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Applies CA Reactor rank, efficiency, and energy modifiers to its selected output recipe. */
public class CAReactorRecipeLogic extends MultiblockRecipeLogic {

    private final CAReactorBlockEntity reactor;

    private boolean workedThisTick;

    public CAReactorRecipeLogic(@NotNull CAReactorBlockEntity reactor,
                                @NotNull RecipeType<MachineRecipe> recipeType,
                                @NotNull ClayEnergyHolder energyHolder) {
        super(reactor, recipeType, energyHolder, reactor::isStructureFormed, () -> reactor.tier);
        this.reactor = reactor;
    }

    @Override
    public void tick() {
        this.workedThisTick = false;
        super.tick();
    }

    @Override
    protected RecipeHolder<?> getMatchedRecipe(Level level, List<ItemStack> inventoryStacks) {
        ItemStack expectedOutput = this.reactor.getResultPureAntimatter();
        for (RecipeHolder<MachineRecipe> holder : level.getRecipeManager().getAllRecipesFor(this.recipeType)) {
            MachineRecipe recipe = holder.value();
            if (recipe.recipeTier() <= this.reactor.tier && recipe.matchesItems(inventoryStacks) &&
                    recipe.outputs().size() == 1 && recipe.outputs().getFirst().is(expectedOutput.getItem())) {
                return holder;
            }
        }
        return null;
    }

    @Override
    protected long getRecipeCEPerTick(RecipeHolder<?> recipeHolder) {
        long baseCEPerTick = super.getRecipeCEPerTick(recipeHolder);
        double effectiveCEPerTick = baseCEPerTick * this.reactor.getCEPerTickMultiplier();
        if (!Double.isFinite(effectiveCEPerTick) || effectiveCEPerTick >= Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        }
        return Math.max(0L, (long) effectiveCEPerTick);
    }

    @Override
    protected long getRecipeDuration(RecipeHolder<?> recipeHolder) {
        double efficiency = this.reactor.getEfficiency();
        if (efficiency <= 0.0D || !Double.isFinite(efficiency)) {
            return super.getRecipeDuration(recipeHolder);
        }
        return Math.max(1L, (long) (super.getRecipeDuration(recipeHolder) / efficiency));
    }

    @Override
    protected void updateWorkingProgress(int virtualTick) {
        boolean hadRecipe = this.processingRecipeHolder != null;
        long previousProgress = this.currentProgress;
        super.updateWorkingProgress(virtualTick);
        if (hadRecipe && (this.processingRecipeHolder == null || this.currentProgress > previousProgress)) {
            this.workedThisTick = true;
        }
    }

    public boolean workedThisTick() {
        return this.workedThisTick;
    }
}
