package net.kogepan.clayium.recipes.display;

import net.kogepan.clayium.Clayium;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ClayiumRecipeDisplays {

    public static final DeferredRegister<RecipeDisplay.Type<?>> RECIPE_DISPLAYS = DeferredRegister
            .create(Registries.RECIPE_DISPLAY, Clayium.MODID);

    public static final DeferredHolder<RecipeDisplay.Type<?>, RecipeDisplay.Type<ClayWorkTableRecipeDisplay>> CLAY_WORK_TABLE = RECIPE_DISPLAYS
            .register("clay_work_table", () -> ClayWorkTableRecipeDisplay.TYPE);
    public static final DeferredHolder<RecipeDisplay.Type<?>, RecipeDisplay.Type<MachineRecipeDisplay>> MACHINE = RECIPE_DISPLAYS
            .register("machine", () -> MachineRecipeDisplay.TYPE);
    public static final DeferredHolder<RecipeDisplay.Type<?>, RecipeDisplay.Type<QuartzCrucibleRecipeDisplay>> QUARTZ_CRUCIBLE = RECIPE_DISPLAYS
            .register("quartz_crucible", () -> QuartzCrucibleRecipeDisplay.TYPE);
    public static final DeferredHolder<RecipeDisplay.Type<?>, RecipeDisplay.Type<LaserIrradiationRecipeDisplay>> LASER_IRRADIATION = RECIPE_DISPLAYS
            .register("laser_irradiation", () -> LaserIrradiationRecipeDisplay.TYPE);

    private ClayiumRecipeDisplays() {}
}
