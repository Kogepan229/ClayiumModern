package net.kogepan.clayium.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.recipes.ClayWorkTableRecipe;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;

public class ClayiumRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE,
            Clayium.MODID);

    public static final Map<ResourceLocation, DeferredHolder<RecipeType<?>, SimpleMachineRecipeType<MachineRecipe>>> SIMPLE_MACHINE_RECIPE_TYPES = new HashMap<>();

    public static DeferredHolder<RecipeType<?>, SimpleMachineRecipeType<MachineRecipe>> registerSimpleMachineType(String name,
                                                                                                                  final int maxInputSize,
                                                                                                                  final int maxOutputSize) {
        DeferredHolder<RecipeType<?>, SimpleMachineRecipeType<MachineRecipe>> holder = RECIPE_TYPES.register(name,
                (r) -> SimpleMachineRecipeType.create(r, maxInputSize, maxOutputSize));
        SIMPLE_MACHINE_RECIPE_TYPES.put(holder.getId(), holder);
        return holder;
    }

    public static final DeferredHolder<RecipeType<?>, RecipeType<ClayWorkTableRecipe>> CLAY_WORK_TABLE_RECIPE_TYPE = RECIPE_TYPES
            .register("clay_work_table", RecipeType::simple);

    public static final DeferredHolder<RecipeType<?>, SimpleMachineRecipeType<MachineRecipe>> BENDING_MACHINE_RECIPE_TYPE = registerSimpleMachineType(
            "bending_machine", 1, 1);
    public static final DeferredHolder<RecipeType<?>, SimpleMachineRecipeType<MachineRecipe>> WIRE_DRAWING_MACHINE_RECIPE_TYPE = registerSimpleMachineType(
            "wire_drawing_machine", 1, 1);
    public static final DeferredHolder<RecipeType<?>, SimpleMachineRecipeType<MachineRecipe>> PIPE_DRAWING_MACHINE_RECIPE_TYPE = registerSimpleMachineType(
            "pipe_drawing_machine", 1, 1);
    public static final DeferredHolder<RecipeType<?>, SimpleMachineRecipeType<MachineRecipe>> CUTTING_MACHINE_RECIPE_TYPE = registerSimpleMachineType(
            "cutting_machine", 1, 1);
    public static final DeferredHolder<RecipeType<?>, SimpleMachineRecipeType<MachineRecipe>> LATHE_RECIPE_TYPE = registerSimpleMachineType(
            "lathe", 1, 1);
    public static final DeferredHolder<RecipeType<?>, SimpleMachineRecipeType<MachineRecipe>> MILLING_MACHINE_RECIPE_TYPE = registerSimpleMachineType(
            "milling_machine", 1, 1);
}
