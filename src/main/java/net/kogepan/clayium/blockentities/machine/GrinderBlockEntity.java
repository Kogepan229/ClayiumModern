package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.SimpleMachineBlockEntity;
import net.kogepan.clayium.blockentities.trait.BaseMultiplierRecipeLogic;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

public class GrinderBlockEntity extends SimpleMachineBlockEntity {

    public GrinderBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.GRINDER_BLOCK_ENTITY.get(), pos, blockState,
                ClayiumRecipeTypes.GRINDER_RECIPE_TYPE.get(),
                (be) -> new BaseMultiplierRecipeLogic(be, ClayiumRecipeTypes.GRINDER_RECIPE_TYPE.get(),
                        be.getEnergyHolder()));
    }
}
