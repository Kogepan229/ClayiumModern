package net.kogepan.clayium.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.recipes.ClayWorkTableRecipe;
import net.kogepan.clayium.recipes.recipes.LaserIrradiationRecipe;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.recipes.recipes.QuartzCrucibleRecipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ClayiumRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
            .create(Registries.RECIPE_SERIALIZER, Clayium.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ClayWorkTableRecipe>> CLAY_WORK_TABLE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS
            .register("clay_work_table",
                    () -> new RecipeSerializer<>(ClayWorkTableRecipe.MAP_CODEC, ClayWorkTableRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MachineRecipe>> MACHINE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS
            .register("machine", () -> new RecipeSerializer<>(MachineRecipe.MAP_CODEC, MachineRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<QuartzCrucibleRecipe>> QUARTZ_CRUCIBLE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS
            .register("quartz_crucible",
                    () -> new RecipeSerializer<>(QuartzCrucibleRecipe.MAP_CODEC, QuartzCrucibleRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LaserIrradiationRecipe>> LASER_IRRADIATION_RECIPE_SERIALIZER = RECIPE_SERIALIZERS
            .register("laser_irradiation",
                    () -> new RecipeSerializer<>(LaserIrradiationRecipe.MAP_CODEC,
                            LaserIrradiationRecipe.STREAM_CODEC));

    private ClayiumRecipeSerializers() {}
}
