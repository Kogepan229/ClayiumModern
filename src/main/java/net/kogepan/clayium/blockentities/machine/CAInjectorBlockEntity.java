package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.SimpleMachineBlockEntity;
import net.kogepan.clayium.blockentities.WorkableClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.trait.AbstractRecipeLogic;
import net.kogepan.clayium.blockentities.trait.CAInjectorRecipeLogic;
import net.kogepan.clayium.blockentities.trait.ClayContainerTrait;
import net.kogepan.clayium.blockentities.trait.ResonanceManager;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import org.jetbrains.annotations.NotNull;

public class CAInjectorBlockEntity extends SimpleMachineBlockEntity {

    public CAInjectorBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.CA_INJECTOR_BLOCK_ENTITY.get(), pos, blockState,
                ClayiumRecipeTypes.CA_INJECTOR_RECIPE_TYPE.get(), CAInjectorBlockEntity::createRecipeLogic);
    }

    private static AbstractRecipeLogic createRecipeLogic(WorkableClayContainerBlockEntity blockEntity) {
        ResonanceManager resonanceManager = new ResonanceManager(blockEntity);
        blockEntity.addTrait(resonanceManager);
        return new CAInjectorRecipeLogic(blockEntity, ClayiumRecipeTypes.CA_INJECTOR_RECIPE_TYPE.get(),
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
