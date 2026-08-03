package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.items.blockitem.StorageContainerBlockItem;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class AssemblerMachineRecipeProvider {

    private AssemblerMachineRecipeProvider() {}

    private static void create(RecipeOutput output, String name, ItemStack input1, ItemStack input2,
                               ItemStack result, long energy, long duration) {
        MachineRecipe recipe = new MachineRecipe(ClayiumRecipeTypes.ASSEMBLER_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(input1), ItemIngredientStack.of(input2)), List.of(result), duration,
                energy, 4);
        output.accept(Clayium.id("assembler/" + name), recipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput output) {
        create(output, "az91d_alloy_hull", ClayiumItems.LARGE_AZ91D_ALLOY_PLATE.toStack(),
                ClayiumItems.PRECISION_CIRCUIT.toStack(), new ItemStack(ClayiumBlocks.AZ91D_ALLOY_HULL.get()),
                energyForTier(6), 120);

        create(output, "milling_machine_1_from_machine_hull", machineHull(1),
                ClayiumItems.DENSE_CLAY_CUTTING_HEAD.toStack(),
                new ItemStack(ClayiumBlocks.MILLING_MACHINE_BLOCKS.get(1).get()), energyForTier(1), 120);
        create(output, "energetic_clay_condenser_3_from_machine_hull", machineHull(3),
                ClayiumItems.CLAY_ENERGY_EXCITOR.toStack(2),
                new ItemStack(ClayiumBlocks.ENERGETIC_CLAY_CONDENSER_BLOCKS.get(3).get()), energyForTier(3), 120);

        for (int tier = 1; tier <= 3; tier++) {
            create(output, "cobblestone_generator_" + tier + "_from_plate", materialLargePlate(tier, 1),
                    ClayiumItems.SIMPLE_CIRCUIT.toStack(),
                    new ItemStack(ClayiumBlocks.COBBLESTONE_GENERATORS.get(tier).get()), energyForTier(tier), 40);
        }
        for (int tier = 1; tier <= 4; tier++) {
            create(output, "bending_machine_" + tier + "_from_machine_hull", machineHull(tier),
                    ClayiumItems.DENSE_CLAY_PLATE.toStack(3),
                    new ItemStack(ClayiumBlocks.BENDING_MACHINE_BLOCKS.get(tier).get()), energyForTier(tier), 120);
            create(output, "wire_drawing_machine_" + tier + "_from_machine_hull", machineHull(tier),
                    ClayiumItems.DENSE_CLAY_PIPE.toStack(2),
                    new ItemStack(ClayiumBlocks.WIRE_DRAWING_MACHINE_BLOCKS.get(tier).get()), energyForTier(tier),
                    120);
            create(output, "pipe_drawing_machine_" + tier + "_from_machine_hull", machineHull(tier),
                    ClayiumItems.DENSE_CLAY_CYLINDER.toStack(2),
                    new ItemStack(ClayiumBlocks.PIPE_DRAWING_MACHINE_BLOCKS.get(tier).get()), energyForTier(tier),
                    120);
            create(output, "cutting_machine_" + tier + "_from_machine_hull", machineHull(tier),
                    ClayiumItems.CLAY_CUTTING_HEAD.toStack(),
                    new ItemStack(ClayiumBlocks.CUTTING_MACHINE_BLOCKS.get(tier).get()), energyForTier(tier), 120);
            create(output, "lathe_" + tier + "_from_machine_hull", machineHull(tier),
                    ClayiumItems.CLAY_SPINDLE.toStack(), new ItemStack(ClayiumBlocks.LATHE_BLOCKS.get(tier).get()),
                    energyForTier(tier), 120);
        }
        for (int tier = 2; tier <= 3; tier++) {
            create(output, "clay_condenser_" + tier + "_from_plate", machineHull(tier),
                    materialLargePlate(tier, 1), new ItemStack(ClayiumBlocks.CLAY_CONDENSER_BLOCKS.get(tier).get()),
                    energyForTier(tier), 120);
        }
        for (int tier = 2; tier <= 4; tier++) {
            create(output, "decomposer_" + tier + "_from_machine_hull", machineHull(tier),
                    ClayiumItems.CLAY_GEAR.toStack(4),
                    new ItemStack(ClayiumBlocks.DECOMPOSER_BLOCKS.get(tier).get()), energyForTier(tier), 120);
        }
        for (int tier = 2; tier <= 6; tier++) {
            create(output, "grinder_" + tier + "_from_machine_hull", machineHull(tier),
                    ClayiumItems.DENSE_CLAY_GRINDING_HEAD.toStack(),
                    new ItemStack(ClayiumBlocks.GRINDER_BLOCKS.get(tier).get()), energyForTier(tier), 120);
        }
        for (int tier = 3; tier <= 4; tier++) {
            create(output, "milling_machine_" + tier + "_from_machine_hull", machineHull(tier),
                    ClayiumItems.DENSE_CLAY_CUTTING_HEAD.toStack(),
                    new ItemStack(ClayiumBlocks.MILLING_MACHINE_BLOCKS.get(tier).get()), energyForTier(tier), 120);
            create(output, "inscriber_" + tier + "_from_assembler",
                    new ItemStack(ClayiumBlocks.ASSEMBLER_BLOCKS.get(tier).get()),
                    ClayiumItems.BASIC_CIRCUIT.toStack(),
                    new ItemStack(ClayiumBlocks.INSCRIBER_BLOCKS.get(tier).get()), energyForTier(tier), 40);
        }
        for (int tier = 3; tier <= 6; tier++) {
            create(output, "centrifuge_" + tier + "_from_machine_hull", machineHull(tier),
                    ClayiumItems.DENSE_CLAY_SPINDLE.toStack(Math.max(tier - 4, 1)),
                    new ItemStack(ClayiumBlocks.CENTRIFUGE_BLOCKS.get(tier).get()), energyForTier(tier), 120);
        }

        for (int tier = 4; tier <= 6; tier++) {
            create(output, "clay_buffer_" + tier, materialPlate(tier, 1), circuitForTier(tier, 1),
                    new ItemStack(ClayiumBlocks.CLAY_BUFFERS.get(tier).get(), 16), energyForTier(tier), 40);
            create(output, "multi_track_buffer_" + tier,
                    new ItemStack(ClayiumBlocks.CLAY_BUFFERS.get(tier).get(), 6), materialLargePlate(tier, 1),
                    new ItemStack(ClayiumBlocks.MULTITRACK_BUFFERS.get(tier).get()), energyForTier(tier), 40);
            create(output, "cobblestone_generator_" + tier + "_from_buffer",
                    new ItemStack(ClayiumBlocks.CLAY_BUFFERS.get(tier).get()), ClayiumItems.SIMPLE_CIRCUIT.toStack(),
                    new ItemStack(ClayiumBlocks.COBBLESTONE_GENERATORS.get(tier).get()), energyForTier(tier), 40);
            create(output, "salt_extractor_" + tier + "_from_buffer",
                    new ItemStack(ClayiumBlocks.CLAY_BUFFERS.get(tier).get()), ClayiumItems.BASIC_CIRCUIT.toStack(),
                    new ItemStack(ClayiumBlocks.SALT_EXTRACTOR_BLOCKS.get(tier).get()), energyForTier(tier), 40);
            create(output, "smelter_" + tier + "_from_machine_hull", machineHull(tier),
                    ClayiumItems.SIMPLE_CIRCUIT.toStack(tier - 3),
                    new ItemStack(ClayiumBlocks.SMELTER_BLOCKS.get(tier).get()), energyForTier(tier), 120);
            if (tier <= 5) {
                create(output, "clay_condenser_" + tier + "_from_buffer", machineHull(tier),
                        new ItemStack(ClayiumBlocks.CLAY_BUFFERS.get(tier).get()),
                        new ItemStack(ClayiumBlocks.CLAY_CONDENSER_BLOCKS.get(tier).get()), energyForTier(tier), 120);
                create(output, "chemical_reactor_" + tier + "_from_machine_hull", machineHull(tier),
                        ClayiumItems.BASIC_CIRCUIT.toStack(tier - 3),
                        new ItemStack(ClayiumBlocks.CHEMICAL_REACTOR_BLOCKS.get(tier).get()), energyForTier(tier),
                        120);
            }
        }

        for (int tier = 5; tier <= 6; tier++) {
            create(output, "clay_interface_" + tier, machineHull(tier),
                    new ItemStack(ClayiumBlocks.CLAY_BUFFERS.get(6).get()),
                    new ItemStack(ClayiumBlocks.CLAY_INTERFACE_BLOCKS.get(tier).get()), energyForTier(tier), 40);
            create(output, "bending_machine_" + tier + "_from_machine_hull", machineHull(tier),
                    ClayiumItems.DENSE_CLAY_PLATE.toStack((tier - 4) * 3),
                    new ItemStack(ClayiumBlocks.BENDING_MACHINE_BLOCKS.get(tier).get()), energyForTier(tier), 120);
        }

        create(output, "solar_clay_fabricator_5", machineHull(5), ClayiumItems.SILICON_PLATE.toStack(8),
                new ItemStack(ClayiumBlocks.SOLAR_CLAY_FABRICATOR_BLOCKS.get(5).get()), energyForTier(5), 120);
        create(output, "auto_clay_condenser_5", new ItemStack(ClayiumBlocks.CLAY_BUFFERS.get(5).get()),
                ClayiumItems.ADVANCED_CIRCUIT.toStack(),
                new ItemStack(ClayiumBlocks.AUTO_CLAY_CONDENSER_BLOCKS.get(5).get()), energyForTier(5), 40);
        create(output, "electrolysis_reactor_6", new ItemStack(ClayiumBlocks.CHEMICAL_REACTOR_BLOCKS.get(5).get()),
                ClayiumItems.PRECISION_CIRCUIT.toStack(),
                new ItemStack(ClayiumBlocks.ELECTROLYSIS_REACTOR_BLOCKS.get(6).get()), energyForTier(6), 40);
        create(output, "solar_clay_fabricator_6", machineHull(6), ClayiumItems.SILICON_PLATE.toStack(16),
                new ItemStack(ClayiumBlocks.SOLAR_CLAY_FABRICATOR_BLOCKS.get(6).get()), energyForTier(6), 120);
        create(output, "clay_blast_furnace", new ItemStack(ClayiumBlocks.SMELTER_BLOCKS.get(6).get()),
                new ItemStack(ClayiumBlocks.CLAY_INTERFACE_BLOCKS.get(6).get()),
                new ItemStack(ClayiumBlocks.CLAY_BLAST_FURNACE.get()), energyForTier(6), 120);

        ItemStack storageContainers = new ItemStack(ClayiumBlocks.STORAGE_CONTAINER.get(), 4);
        StorageContainerBlockItem.setCapacity(storageContainers, 65_536);
        create(output, "storage_container", new ItemStack(ClayiumBlocks.AZ91D_ALLOY_HULL.get(), 4),
                new ItemStack(ClayiumBlocks.CLAY_INTERFACE_BLOCKS.get(5).get()), storageContainers, energyForTier(6),
                120);
    }

    private static ItemStack machineHull(int tier) {
        return new ItemStack(ClayiumBlocks.MACHINE_HULLS.get(tier).get());
    }

    private static ItemStack materialPlate(int tier, int count) {
        return switch (tier) {
            case 1 -> ClayiumItems.CLAY_PLATE.toStack(count);
            case 2 -> ClayiumItems.DENSE_CLAY_PLATE.toStack(count);
            case 3 -> ClayiumItems.INDUSTRIAL_CLAY_PLATE.toStack(count);
            case 4 -> ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_PLATE.toStack(count);
            case 5 -> ClayiumItems.IMPURE_SILICON_PLATE.toStack(count);
            case 6 -> ClayiumItems.ALUMINIUM_PLATE.toStack(count);
            default -> throw new IllegalArgumentException("Unsupported material tier: " + tier);
        };
    }

    private static ItemStack materialLargePlate(int tier, int count) {
        return switch (tier) {
            case 1 -> ClayiumItems.LARGE_CLAY_PLATE.toStack(count);
            case 2 -> ClayiumItems.DENSE_LARGE_CLAY_PLATE.toStack(count);
            case 3 -> ClayiumItems.LARGE_INDUSTRIAL_CLAY_PLATE.toStack(count);
            case 4 -> ClayiumItems.LARGE_ADVANCED_INDUSTRIAL_CLAY_PLATE.toStack(count);
            case 5 -> ClayiumItems.LARGE_IMPURE_SILICON_PLATE.toStack(count);
            case 6 -> ClayiumItems.LARGE_ALUMINIUM_PLATE.toStack(count);
            default -> throw new IllegalArgumentException("Unsupported material tier: " + tier);
        };
    }

    private static ItemStack circuitForTier(int tier, int count) {
        return switch (tier) {
            case 4 -> ClayiumItems.BASIC_CIRCUIT.toStack(count);
            case 5 -> ClayiumItems.ADVANCED_CIRCUIT.toStack(count);
            case 6 -> ClayiumItems.PRECISION_CIRCUIT.toStack(count);
            default -> throw new IllegalArgumentException("Unsupported circuit tier: " + tier);
        };
    }

    private static long energyForTier(int tier) {
        return switch (tier) {
            case 1 -> 0;
            case 2 -> CEUtils.TEN_MICRO_CE;
            case 3 -> CEUtils.TEN_MICRO_CE * 10;
            case 4 -> CEUtils.ONE_MILLI_CE;
            case 5 -> CEUtils.ONE_MILLI_CE * 10;
            case 6 -> CEUtils.ONE_MILLI_CE * 100;
            default -> throw new IllegalArgumentException("Unsupported machine tier: " + tier);
        };
    }
}
