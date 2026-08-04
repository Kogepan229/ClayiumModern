package net.kogepan.clayium.gametest;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blockentities.WorkableClayContainerBlockEntity;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.SimpleMachineRecipeType;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

@GameTestHolder(Clayium.MODID)
public final class SingleBlockMachineGameTests {

    private static final String BATCH = Clayium.MODID + ".single_block_machines";
    private static final String EMPTY_STRUCTURE = Clayium.MODID + ":empty";
    private static final String TEST_NAME_PREFIX = "singleblockmachinegametests.";
    private static final int MAX_TICKS = 100;
    private static final BlockPos MACHINE_POS = new BlockPos(2, 2, 2);
    private static final long INITIAL_ENERGY = Long.MAX_VALUE;

    private static final List<MachineTestDefinition> TEST_DEFINITIONS = List.of(
            definition("bending_machine", () -> ClayiumBlocks.BENDING_MACHINE_BLOCKS.get(9).get(),
                    () -> ClayiumRecipeTypes.BENDING_MACHINE_RECIPE_TYPE.get()),
            definition("wire_drawing_machine", () -> ClayiumBlocks.WIRE_DRAWING_MACHINE_BLOCKS.get(4).get(),
                    () -> ClayiumRecipeTypes.WIRE_DRAWING_MACHINE_RECIPE_TYPE.get()),
            definition("pipe_drawing_machine", () -> ClayiumBlocks.PIPE_DRAWING_MACHINE_BLOCKS.get(4).get(),
                    () -> ClayiumRecipeTypes.PIPE_DRAWING_MACHINE_RECIPE_TYPE.get()),
            definition("cutting_machine", () -> ClayiumBlocks.CUTTING_MACHINE_BLOCKS.get(4).get(),
                    () -> ClayiumRecipeTypes.CUTTING_MACHINE_RECIPE_TYPE.get()),
            definition("lathe", () -> ClayiumBlocks.LATHE_BLOCKS.get(4).get(),
                    () -> ClayiumRecipeTypes.LATHE_RECIPE_TYPE.get()),
            definition("milling_machine", () -> ClayiumBlocks.MILLING_MACHINE_BLOCKS.get(4).get(),
                    () -> ClayiumRecipeTypes.MILLING_MACHINE_RECIPE_TYPE.get()),
            definition("clay_condenser", () -> ClayiumBlocks.CLAY_CONDENSER_BLOCKS.get(10).get(),
                    () -> ClayiumRecipeTypes.CLAY_CONDENSER_RECIPE_TYPE.get()),
            definition("energetic_clay_condenser",
                    () -> ClayiumBlocks.ENERGETIC_CLAY_CONDENSER_BLOCKS.get(3).get(),
                    () -> ClayiumRecipeTypes.ENERGETIC_CLAY_CONDENSER_RECIPE_TYPE.get()),
            definition("grinder", () -> ClayiumBlocks.GRINDER_BLOCKS.get(10).get(),
                    () -> ClayiumRecipeTypes.GRINDER_RECIPE_TYPE.get()),
            definition("centrifuge", () -> ClayiumBlocks.CENTRIFUGE_BLOCKS.get(6).get(),
                    () -> ClayiumRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get()),
            definition("smelter", () -> ClayiumBlocks.SMELTER_BLOCKS.get(9).get(),
                    () -> ClayiumRecipeTypes.SMELTER_RECIPE_TYPE.get()),
            definition("alloy_smelter", () -> ClayiumBlocks.ALLOY_SMELTER_BLOCKS.get(6).get(),
                    () -> ClayiumRecipeTypes.ALLOY_SMELTER_RECIPE_TYPE.get()),
            definition("decomposer", () -> ClayiumBlocks.DECOMPOSER_BLOCKS.get(4).get(),
                    () -> ClayiumRecipeTypes.DECOMPOSER_RECIPE_TYPE.get()),
            definition("electrolysis_reactor", () -> ClayiumBlocks.ELECTROLYSIS_REACTOR_BLOCKS.get(9).get(),
                    () -> ClayiumRecipeTypes.ELECTROLYSIS_REACTOR_RECIPE_TYPE.get()),
            definition("inscriber", () -> ClayiumBlocks.INSCRIBER_BLOCKS.get(4).get(),
                    () -> ClayiumRecipeTypes.INSCRIBER_RECIPE_TYPE.get()),
            definition("assembler", () -> ClayiumBlocks.ASSEMBLER_BLOCKS.get(10).get(),
                    () -> ClayiumRecipeTypes.ASSEMBLER_RECIPE_TYPE.get()),
            definition("chemical_reactor", () -> ClayiumBlocks.CHEMICAL_REACTOR_BLOCKS.get(8).get(),
                    () -> ClayiumRecipeTypes.CHEMICAL_REACTOR_RECIPE_TYPE.get()),
            definition("ca_injector", () -> ClayiumBlocks.CA_INJECTOR_BLOCKS.get(13).get(),
                    () -> ClayiumRecipeTypes.CA_INJECTOR_RECIPE_TYPE.get()),
            definition("ca_condenser", () -> ClayiumBlocks.CA_CONDENSER_BLOCKS.get(11).get(),
                    () -> ClayiumRecipeTypes.CA_CONDENSER_RECIPE_TYPE.get()),
            definition("matter_transformer", () -> ClayiumBlocks.MATTER_TRANSFORMER_BLOCKS.get(12).get(),
                    () -> ClayiumRecipeTypes.MATTER_TRANSFORMER_RECIPE_TYPE.get()));

    private SingleBlockMachineGameTests() {}

    @GameTestGenerator
    public static Collection<TestFunction> generateTests() {
        return TEST_DEFINITIONS.stream()
                .map(definition -> new TestFunction(
                        BATCH,
                        TEST_NAME_PREFIX + definition.name(),
                        EMPTY_STRUCTURE,
                        MAX_TICKS,
                        0L,
                        true,
                        helper -> runRecipeProcessingTest(helper, definition)))
                .toList();
    }

    private static void runRecipeProcessingTest(GameTestHelper helper, MachineTestDefinition definition) {
        helper.setBlock(MACHINE_POS, definition.machineBlock().get());
        for (Direction direction : Direction.values()) {
            helper.setBlock(MACHINE_POS.relative(direction), ClayiumBlocks.OVERCLOCKERS.get(13).get());
        }

        BlockEntity blockEntity = helper.getBlockEntity(MACHINE_POS);
        if (!(blockEntity instanceof WorkableClayContainerBlockEntity machine)) {
            helper.fail("Expected a workable machine block entity", MACHINE_POS);
            return;
        }

        machine.initDefaultRoutes();
        machine.refreshOverclockFactor();

        RecipeHolder<MachineRecipe> recipeHolder = selectRecipe(machine, definition.recipeType().get());
        if (recipeHolder == null) {
            helper.fail("No usable recipe found for " + definition.name(), MACHINE_POS);
            return;
        }

        MachineRecipe recipe = recipeHolder.value();
        insertRecipeInputs(machine.getInputInventory(), recipe.inputs());
        machine.getEnergyHolder().addEnergy(INITIAL_ENERGY);
        List<ItemStack> expectedOutputs = recipe.copyOutputs();

        helper.succeedWhen(() -> {
            assertInputsConsumed(helper, machine.getInputInventory(), recipeHolder);
            assertOutputsEqual(helper, machine.getOutputInventory(), expectedOutputs, recipeHolder);
            if (recipe.cePerTick() > 0 && machine.getEnergyHolder().getEnergyStored() >= INITIAL_ENERGY) {
                helper.fail("Recipe did not consume CE: " + recipeHolder.id(), MACHINE_POS);
            }
        });
    }

    private static RecipeHolder<MachineRecipe> selectRecipe(
                                                            WorkableClayContainerBlockEntity machine,
                                                            SimpleMachineRecipeType<MachineRecipe> recipeType) {
        return machine.getLevel().getRecipeManager().getAllRecipesFor(recipeType).stream()
                .filter(holder -> holder.value().recipeTier() <= machine.tier)
                .filter(holder -> canCreateInputs(holder.value(), machine.getInputInventory().getSlots()))
                .min(Comparator.comparingLong((RecipeHolder<MachineRecipe> holder) -> holder.value().duration())
                        .thenComparing(holder -> holder.id().toString()))
                .orElse(null);
    }

    private static boolean canCreateInputs(MachineRecipe recipe, int inputSlots) {
        if (recipe.inputs().size() > inputSlots) {
            return false;
        }

        List<ItemStack> stacks = createRecipeInputs(recipe.inputs());
        return stacks.size() == recipe.inputs().size() && recipe.matchesItems(mergeEqualStacks(stacks));
    }

    private static List<ItemStack> createRecipeInputs(List<ItemIngredientStack> ingredients) {
        List<ItemStack> stacks = new ArrayList<>(ingredients.size());
        for (ItemIngredientStack ingredient : ingredients) {
            ItemStack[] matchingStacks = ingredient.getIngredient().getItems();
            if (matchingStacks.length == 0 || ingredient.getAmount() > matchingStacks[0].getMaxStackSize()) {
                return List.of();
            }

            ItemStack stack = matchingStacks[0].copy();
            stack.setCount(ingredient.getAmount());
            stacks.add(stack);
        }
        return stacks;
    }

    private static List<ItemStack> mergeEqualStacks(List<ItemStack> stacks) {
        List<ItemStack> merged = new ArrayList<>();
        for (ItemStack stack : stacks) {
            ItemStack matchingStack = merged.stream()
                    .filter(candidate -> ItemStack.isSameItemSameComponents(candidate, stack))
                    .findFirst()
                    .orElse(null);
            if (matchingStack == null) {
                merged.add(stack.copy());
            } else {
                matchingStack.grow(stack.getCount());
            }
        }
        return merged;
    }

    private static void insertRecipeInputs(
                                           IItemHandlerModifiable inputInventory,
                                           List<ItemIngredientStack> ingredients) {
        List<ItemStack> stacks = createRecipeInputs(ingredients);
        for (int slot = 0; slot < stacks.size(); slot++) {
            inputInventory.setStackInSlot(slot, stacks.get(slot));
        }
    }

    private static void assertInputsConsumed(
                                             GameTestHelper helper, IItemHandler inputInventory,
                                             RecipeHolder<MachineRecipe> recipeHolder) {
        for (int slot = 0; slot < inputInventory.getSlots(); slot++) {
            if (!inputInventory.getStackInSlot(slot).isEmpty()) {
                helper.fail("Recipe inputs were not fully consumed: " + recipeHolder.id(), MACHINE_POS);
            }
        }
    }

    private static void assertOutputsEqual(
                                           GameTestHelper helper,
                                           IItemHandler outputInventory,
                                           List<ItemStack> expectedOutputs,
                                           RecipeHolder<MachineRecipe> recipeHolder) {
        for (ItemStack expected : expectedOutputs) {
            int expectedCount = totalMatchingCount(expectedOutputs, expected);
            int actualCount = totalMatchingCount(outputInventory, expected);
            if (actualCount != expectedCount) {
                helper.fail(
                        "Incorrect recipe output for " + recipeHolder.id() + ": expected " +
                                stackDescription(expected, expectedCount) + ", got " + actualCount,
                        MACHINE_POS);
            }
        }

        for (int slot = 0; slot < outputInventory.getSlots(); slot++) {
            ItemStack actual = outputInventory.getStackInSlot(slot);
            if (!actual.isEmpty() && expectedOutputs.stream()
                    .noneMatch(expected -> ItemStack.isSameItemSameComponents(expected, actual))) {
                helper.fail(
                        "Unexpected recipe output for " + recipeHolder.id() + ": " +
                                stackDescription(actual, actual.getCount()),
                        MACHINE_POS);
            }
        }
    }

    private static int totalMatchingCount(List<ItemStack> stacks, ItemStack target) {
        return stacks.stream()
                .filter(stack -> ItemStack.isSameItemSameComponents(stack, target))
                .mapToInt(ItemStack::getCount)
                .sum();
    }

    private static int totalMatchingCount(IItemHandler inventory, ItemStack target) {
        int count = 0;
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (ItemStack.isSameItemSameComponents(stack, target)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static String stackDescription(ItemStack stack, int count) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()) + " x" + count;
    }

    private static MachineTestDefinition definition(
                                                    String name,
                                                    Supplier<? extends Block> machineBlock,
                                                    Supplier<SimpleMachineRecipeType<MachineRecipe>> recipeType) {
        return new MachineTestDefinition(name, machineBlock, recipeType);
    }

    private record MachineTestDefinition(
                                         String name,
                                         Supplier<? extends Block> machineBlock,
                                         Supplier<SimpleMachineRecipeType<MachineRecipe>> recipeType) {}
}
