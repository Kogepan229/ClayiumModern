package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

/**
 * Recipe logic for Clay Blast Furnace.
 * Processing is available only when the multiblock structure is formed.
 */
public class BlastFurnaceRecipeLogic extends EnergyRecipeLogic {

    private final BooleanSupplier structureFormedSupplier;
    private final IntSupplier structureTierSupplier;

    public BlastFurnaceRecipeLogic(@NotNull ClayContainerBlockEntity blockEntity,
                                   @NotNull RecipeType<MachineRecipe> recipeType,
                                   @NotNull ClayEnergyHolder energyHolder,
                                   @NotNull BooleanSupplier structureFormedSupplier,
                                   @NotNull IntSupplier structureTierSupplier) {
        super(blockEntity, recipeType, energyHolder);
        this.structureFormedSupplier = structureFormedSupplier;
        this.structureTierSupplier = structureTierSupplier;
    }

    @Override
    public void tick() {
        Level level = this.blockEntity.getLevel();
        if (level == null || level.isClientSide()) return;

        if (!this.structureFormedSupplier.getAsBoolean()) {
            invalidateProgress();
            return;
        }
        this.canProgress = true;
        super.tick();
    }

    @Override
    protected boolean checkCanProgress() {
        this.canProgress = this.structureFormedSupplier.getAsBoolean();
        return this.canProgress;
    }

    @Override
    protected boolean shouldSearchForRecipe() {
        return this.structureFormedSupplier.getAsBoolean() && super.shouldSearchForRecipe();
    }

    @Override
    protected RecipeHolder<?> getMatchedRecipe(Level level, List<ItemStack> inventoryStacks) {
        int recipeTier = this.structureTierSupplier.getAsInt();
        var recipeHolders = level.getRecipeManager().getAllRecipesFor(this.recipeType);
        for (RecipeHolder<MachineRecipe> holder : recipeHolders) {
            MachineRecipe recipe = holder.value();
            if (recipe.recipeTier() <= recipeTier && recipe.matchesItems(inventoryStacks)) {
                return holder;
            }
        }
        return null;
    }

    public void invalidateProgress() {
        this.processingRecipeHolder = null;
        this.pendingRecipeId = null;
        this.currentProgress = 0;
        this.canProgress = false;
    }
}
