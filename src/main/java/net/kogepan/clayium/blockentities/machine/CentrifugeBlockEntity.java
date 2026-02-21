package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.WorkableClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.trait.CentrifugeRecipeLogic;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import com.lowdragmc.lowdraglib2.gui.slot.ItemHandlerSlot;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import org.appliedenergistics.yoga.YogaFlexDirection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CentrifugeBlockEntity extends WorkableClayContainerBlockEntity {

    public CentrifugeBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.CENTRIFUGE_BLOCK_ENTITY.get(), pos, blockState,
                getValidInputModes(ClayiumRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get().getMaxInputSize()),
                List.of(MachineIOMode.ALL),
                ClayiumRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get().getMaxInputSize(),
                getOutputSize(blockState),
                (be) -> new CentrifugeRecipeLogic(be, ClayiumRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get(),
                        be.getEnergyHolder()),
                ClayiumRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get());
    }

    private static int getOutputSize(BlockState blockState) {
        if (blockState.getBlock() instanceof ClayContainerBlock containerBlock) {
            int tier = containerBlock.tier;
            return switch (tier) {
                case 3 -> 1;
                case 4 -> 2;
                case 5 -> 3;
                case 6 -> 4;
                default -> throw new IllegalArgumentException();
            };
        }
        throw new IllegalArgumentException();
    }

    @Override
    protected UIElement createOutputSlots() {
        if (this.outputItemInventory.getSlots() <= 3) {
            UIElement outputSlotContainer = new UIElement()
                    .layout(layout -> layout.flexDirection(YogaFlexDirection.ROW));
            for (int i = 0; i < this.outputItemInventory.getSlots(); i++) {
                outputSlotContainer.addChild(
                        new ItemSlot()
                                .bind(new ItemHandlerSlot(this.outputItemInventory, i).setCanPlace((s) -> false)));
            }
            return outputSlotContainer;
        } else {
            UIElement outputSlotContainer = new UIElement();
            UIElement row1 = new UIElement().layout(layout -> layout.flexDirection(YogaFlexDirection.ROW));
            UIElement row2 = new UIElement().layout(layout -> layout.flexDirection(YogaFlexDirection.ROW));

            row1.addChild(
                    new ItemSlot().bind(new ItemHandlerSlot(this.outputItemInventory, 0).setCanPlace((s) -> false)));
            row1.addChild(
                    new ItemSlot().bind(new ItemHandlerSlot(this.outputItemInventory, 1).setCanPlace((s) -> false)));
            row2.addChild(
                    new ItemSlot().bind(new ItemHandlerSlot(this.outputItemInventory, 2).setCanPlace((s) -> false)));
            row2.addChild(
                    new ItemSlot().bind(new ItemHandlerSlot(this.outputItemInventory, 3).setCanPlace((s) -> false)));

            outputSlotContainer.addChild(row1).addChild(row2);
            return outputSlotContainer;
        }
    }
}
