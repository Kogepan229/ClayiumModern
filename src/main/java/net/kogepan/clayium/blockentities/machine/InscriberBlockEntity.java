package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.SimpleMachineBlockEntity;
import net.kogepan.clayium.blockentities.trait.BaseMultiplierRecipeLogic;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

public class InscriberBlockEntity extends SimpleMachineBlockEntity {

    public InscriberBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.INSCRIBER_BLOCK_ENTITY.get(), pos, blockState,
                ClayiumRecipeTypes.INSCRIBER_RECIPE_TYPE.get(),
                (be) -> new BaseMultiplierRecipeLogic(be, ClayiumRecipeTypes.INSCRIBER_RECIPE_TYPE.get(),
                        be.getEnergyHolder()));
    }
}
