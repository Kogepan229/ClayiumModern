package net.kogepan.clayium.recipes.recipes;

import net.kogepan.clayium.recipes.ClayiumRecipeSerializers;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.WeightedItemStack;
import net.kogepan.clayium.recipes.inputs.MachineRecipeInput;
import net.kogepan.clayium.utils.ProgressionRates;

import net.minecraft.core.HolderLookup;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record WeightedMachineRecipe(
                                    @NotNull List<ItemIngredientStack> inputs,
                                    @NotNull List<WeightedItemStack> weightedOutputs,
                                    long duration,
                                    long cePerTick,
                                    int recipeTier)
        implements Recipe<MachineRecipeInput> {

    public WeightedMachineRecipe {
        inputs = List.copyOf(inputs);
        weightedOutputs = List.copyOf(weightedOutputs);
        if (weightedOutputs.isEmpty()) {
            throw new IllegalArgumentException("Weighted outputs must not be empty");
        }
    }

    @Override
    public boolean matches(@NotNull MachineRecipeInput input, @NotNull Level level) {
        List<ItemStack> inputItems = new ArrayList<>(input.size());
        for (int i = 0; i < input.size(); i++) {
            inputItems.add(input.getItem(i));
        }
        return matchesItems(inputItems);
    }

    public boolean matchesItems(@NotNull List<ItemStack> inputItems) {
        for (ItemIngredientStack ingredient : this.inputs) {
            boolean matched = false;
            for (ItemStack inputItem : inputItems) {
                if (ingredient.test(inputItem) && inputItem.getCount() >= ingredient.getAmount()) {
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }
        return true;
    }

    public int totalWeight() {
        int total = 0;
        for (WeightedItemStack weightedOutput : weightedOutputs) {
            total = Math.addExact(total, weightedOutput.weight());
        }
        return total;
    }

    public List<ItemStack> possibleOutputs() {
        return weightedOutputs.stream().map(WeightedItemStack::result).toList();
    }

    public ItemStack rollOutput(@NotNull RandomSource random) {
        int roll = random.nextInt(totalWeight());
        for (WeightedItemStack weightedOutput : weightedOutputs) {
            if (roll < weightedOutput.weight()) {
                return weightedOutput.result().copy();
            }
            roll -= weightedOutput.weight();
        }
        throw new IllegalStateException("Failed to select a weighted output");
    }

    public long adjustedDuration() {
        return ProgressionRates.divideLong(this.duration);
    }

    public long adjustedTotalCE() {
        return this.cePerTick * this.adjustedDuration();
    }

    @Override
    @NotNull
    public ItemStack assemble(@NotNull MachineRecipeInput input, @NotNull HolderLookup.Provider provider) {
        return weightedOutputs.getFirst().result().copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    @NotNull
    public ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        return weightedOutputs.getFirst().result();
    }

    @Override
    @NotNull
    public RecipeSerializer<?> getSerializer() {
        return ClayiumRecipeSerializers.WEIGHTED_MACHINE_RECIPE_SERIALIZER.get();
    }

    @Override
    @NotNull
    public RecipeType<?> getType() {
        return ClayiumRecipeTypes.CHEMICAL_METAL_SEPARATOR_RECIPE_TYPE.get();
    }
}
