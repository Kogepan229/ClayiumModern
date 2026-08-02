package net.kogepan.clayium.recipes.recipes;

import net.kogepan.clayium.recipes.ClayiumRecipeSerializers;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.SimpleMachineRecipeType;
import net.kogepan.clayium.recipes.display.ClayiumRecipeBookCategories;
import net.kogepan.clayium.recipes.display.MachineRecipeDisplay;
import net.kogepan.clayium.recipes.inputs.MachineRecipeInput;
import net.kogepan.clayium.utils.ProgressionRates;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

public record MachineRecipe(SimpleMachineRecipeType<MachineRecipe> recipeType,
                            List<ItemIngredientStack> inputs,
                            List<ItemStackTemplate> outputs,
                            long duration,
                            long cePerTick,
                            int recipeTier)
        implements Recipe<MachineRecipeInput> {

    public static final MapCodec<MachineRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SimpleMachineRecipeType.CODEC.fieldOf("recipe_type").forGetter(MachineRecipe::recipeType),
            ItemIngredientStack.CODEC.listOf().fieldOf("inputs").forGetter(MachineRecipe::inputs),
            ItemStackTemplate.CODEC.listOf().fieldOf("outputs").forGetter(MachineRecipe::outputs),
            Codec.LONG.fieldOf("duration").forGetter(MachineRecipe::duration),
            Codec.LONG.fieldOf("ce_per_tick").forGetter(MachineRecipe::cePerTick),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("recipe_tier").forGetter(MachineRecipe::recipeTier))
            .apply(instance, MachineRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MachineRecipe> STREAM_CODEC = StreamCodec.composite(
            SimpleMachineRecipeType.STREAM_CODEC,
            MachineRecipe::recipeType,
            ItemIngredientStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
            MachineRecipe::inputs,
            ItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs.list()),
            MachineRecipe::outputs,
            ByteBufCodecs.VAR_LONG,
            MachineRecipe::duration,
            ByteBufCodecs.VAR_LONG,
            MachineRecipe::cePerTick,
            ByteBufCodecs.VAR_INT,
            MachineRecipe::recipeTier,
            MachineRecipe::new);

    public MachineRecipe {
        inputs = List.copyOf(inputs);
        outputs = List.copyOf(outputs);
    }

    @Override
    public boolean matches(MachineRecipeInput input, Level level) {
        return this.matches(input, Integer.MAX_VALUE);
    }

    public boolean matches(MachineRecipeInput input, int machineTier) {
        if (this.recipeTier > machineTier) {
            return false;
        }

        int ingredientCount = this.inputs.size();
        int stackCount = input.size();
        int source = 0;
        int firstIngredient = 1;
        int firstStack = firstIngredient + ingredientCount;
        int sink = firstStack + stackCount;
        long[][] capacity = new long[sink + 1][sink + 1];
        long required = 0L;

        for (int ingredientIndex = 0; ingredientIndex < ingredientCount; ingredientIndex++) {
            ItemIngredientStack ingredient = this.inputs.get(ingredientIndex);
            int ingredientNode = firstIngredient + ingredientIndex;
            capacity[source][ingredientNode] = ingredient.amount();
            required += ingredient.amount();

            for (int stackIndex = 0; stackIndex < stackCount; stackIndex++) {
                if (ingredient.test(input.getItem(stackIndex))) {
                    capacity[ingredientNode][firstStack + stackIndex] = ingredient.amount();
                }
            }
        }

        for (int stackIndex = 0; stackIndex < stackCount; stackIndex++) {
            capacity[firstStack + stackIndex][sink] = input.getItem(stackIndex).getCount();
        }

        return maximumFlow(capacity, source, sink) == required;
    }

    private static long maximumFlow(long[][] capacity, int source, int sink) {
        long flow = 0L;
        int[] parent = new int[capacity.length];

        while (findAugmentingPath(capacity, source, sink, parent)) {
            long pathCapacity = Long.MAX_VALUE;
            for (int node = sink; node != source; node = parent[node]) {
                pathCapacity = Math.min(pathCapacity, capacity[parent[node]][node]);
            }
            for (int node = sink; node != source; node = parent[node]) {
                int previous = parent[node];
                capacity[previous][node] -= pathCapacity;
                capacity[node][previous] += pathCapacity;
            }
            flow += pathCapacity;
        }
        return flow;
    }

    private static boolean findAugmentingPath(long[][] capacity, int source, int sink, int[] parent) {
        Arrays.fill(parent, -1);
        parent[source] = source;
        var queue = new ArrayDeque<Integer>();
        queue.add(source);

        while (!queue.isEmpty()) {
            int node = queue.removeFirst();
            for (int next = 0; next < capacity.length; next++) {
                if (parent[next] != -1 || capacity[node][next] <= 0L) {
                    continue;
                }
                parent[next] = node;
                if (next == sink) {
                    return true;
                }
                queue.addLast(next);
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(MachineRecipeInput input) {
        return this.outputs.isEmpty() ? ItemStack.EMPTY : this.outputs.getFirst().create();
    }

    public List<ItemStack> createOutputs() {
        return this.outputs.stream().map(ItemStackTemplate::create).toList();
    }

    public long adjustedDuration() {
        return this.adjustedDuration(ProgressionRates.current());
    }

    public long adjustedDuration(double progressionRate) {
        return ProgressionRates.divideLong(this.duration, progressionRate);
    }

    public long adjustedTotalCE() {
        return this.cePerTick * this.adjustedDuration();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<MachineRecipe> getSerializer() {
        return ClayiumRecipeSerializers.MACHINE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<MachineRecipe> getType() {
        return this.recipeType;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(this.inputs.stream().map(ItemIngredientStack::ingredient).toList());
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new MachineRecipeDisplay(
                this.recipeType.id(),
                this.inputs.stream().map(ItemIngredientStack::display).toList(),
                this.outputs.stream().map(SlotDisplay.ItemStackSlotDisplay::new).map(SlotDisplay.class::cast).toList(),
                this.adjustedDuration(),
                this.cePerTick,
                this.recipeTier));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ClayiumRecipeBookCategories.CLAYIUM.get();
    }
}
