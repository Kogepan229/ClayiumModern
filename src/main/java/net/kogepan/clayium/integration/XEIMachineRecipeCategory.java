package net.kogepan.clayium.integration;

import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.SimpleMachineRecipeType;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class XEIMachineRecipeCategory {

    @Getter
    SimpleMachineRecipeType<MachineRecipe> recipeType;

    @Getter
    Collection<DeferredBlock<ClayContainerBlock>> workstations;

    public XEIMachineRecipeCategory(DeferredHolder<RecipeType<?>, SimpleMachineRecipeType<MachineRecipe>> recipeTypeHolder,
                                    Collection<DeferredBlock<ClayContainerBlock>> workstations) {
        this.recipeType = recipeTypeHolder.get();
        this.workstations = workstations.stream().sorted(Comparator.comparingInt(holder -> holder.get().tier)).toList();
    }

    public ResourceLocation getId() {
        return this.recipeType.getId();
    }

    public DeferredBlock<ClayContainerBlock> getIcon() {
        return this.workstations.stream().findFirst().orElseThrow();
    }

    public static List<XEIMachineRecipeCategory> getCategories() {
        List<XEIMachineRecipeCategory> categories = new ArrayList<>();

        categories.add(new XEIMachineRecipeCategory(ClayiumRecipeTypes.BENDING_MACHINE_RECIPE_TYPE,
                ClayiumBlocks.BENDING_MACHINE_BLOCKS.values()));
        categories.add(new XEIMachineRecipeCategory(ClayiumRecipeTypes.WIRE_DRAWING_MACHINE_RECIPE_TYPE,
                ClayiumBlocks.WIRE_DRAWING_MACHINE_BLOCKS.values()));
        categories.add(new XEIMachineRecipeCategory(ClayiumRecipeTypes.PIPE_DRAWING_MACHINE_RECIPE_TYPE,
                ClayiumBlocks.PIPE_DRAWING_MACHINE_BLOCKS.values()));
        categories.add(new XEIMachineRecipeCategory(ClayiumRecipeTypes.CUTTING_MACHINE_RECIPE_TYPE,
                ClayiumBlocks.CUTTING_MACHINE_BLOCKS.values()));
        categories.add(new XEIMachineRecipeCategory(ClayiumRecipeTypes.LATHE_RECIPE_TYPE,
                ClayiumBlocks.LATHE_BLOCKS.values()));
        categories.add(new XEIMachineRecipeCategory(ClayiumRecipeTypes.MILLING_MACHINE_RECIPE_TYPE,
                ClayiumBlocks.MILLING_MACHINE_BLOCKS.values()));
        categories.add(new XEIMachineRecipeCategory(ClayiumRecipeTypes.CLAY_CONDENSER_RECIPE_TYPE,
                ClayiumBlocks.CLAY_CONDENSER_BLOCKS.values()));
        categories.add(new XEIMachineRecipeCategory(ClayiumRecipeTypes.GRINDER_RECIPE_TYPE,
                ClayiumBlocks.GRINDER_BLOCKS.values()));
        categories.add(new XEIMachineRecipeCategory(ClayiumRecipeTypes.CENTRIFUGE_RECIPE_TYPE,
                ClayiumBlocks.CENTRIFUGE_BLOCKS.values()));
        categories.add(new XEIMachineRecipeCategory(ClayiumRecipeTypes.CHEMICAL_REACTOR_RECIPE_TYPE,
                ClayiumBlocks.CHEMICAL_REACTOR_BLOCKS.values()));
        categories.add(new XEIMachineRecipeCategory(ClayiumRecipeTypes.DECOMPOSER_RECIPE_TYPE,
                ClayiumBlocks.DECOMPOSER_BLOCKS.values()));
        categories.add(new XEIMachineRecipeCategory(ClayiumRecipeTypes.INSCRIBER_RECIPE_TYPE,
                ClayiumBlocks.INSCRIBER_BLOCKS.values()));
        categories.add(new XEIMachineRecipeCategory(ClayiumRecipeTypes.SMELTER_RECIPE_TYPE,
                ClayiumBlocks.SMELTER_BLOCKS.values()));

        return categories;
    }
}
