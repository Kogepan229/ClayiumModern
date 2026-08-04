package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.SimpleMachineBlockEntity;
import net.kogepan.clayium.blockentities.WorkableClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.trait.AbstractRecipeLogic;
import net.kogepan.clayium.blockentities.trait.CACondenserRecipeLogic;
import net.kogepan.clayium.blockentities.trait.ClayContainerTrait;
import net.kogepan.clayium.blockentities.trait.ResonanceManager;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import org.jetbrains.annotations.NotNull;

public class CACondenserBlockEntity extends SimpleMachineBlockEntity {

    public CACondenserBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.CA_CONDENSER_BLOCK_ENTITY.get(), pos, blockState,
                ClayiumRecipeTypes.CA_CONDENSER_RECIPE_TYPE.get(), CACondenserBlockEntity::createRecipeLogic);
    }

    private static AbstractRecipeLogic createRecipeLogic(WorkableClayContainerBlockEntity blockEntity) {
        ResonanceManager resonanceManager = new ResonanceManager(blockEntity);
        blockEntity.addTrait(resonanceManager);
        return new CACondenserRecipeLogic(blockEntity, ClayiumRecipeTypes.CA_CONDENSER_RECIPE_TYPE.get(),
                blockEntity.getEnergyHolder(), resonanceManager);
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        super.createMainUI(holder, root);
        ClayContainerTrait trait = this.getTrait(ResonanceManager.TRAIT_ID);
        if (trait instanceof ResonanceManager resonanceManager) {
            root.addChild(resonanceManager.createResonanceUIElement());
        }
    }
}
