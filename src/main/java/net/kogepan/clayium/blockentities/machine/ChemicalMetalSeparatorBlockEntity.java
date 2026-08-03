package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.WorkableClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.trait.WeightedMachineRecipeLogic;
import net.kogepan.clayium.client.ldlib.elements.LargeItemSlot;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import com.lowdragmc.lowdraglib2.gui.slot.ItemHandlerSlot;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import dev.vfyjxf.taffy.style.FlexDirection;
import org.jetbrains.annotations.NotNull;

public class ChemicalMetalSeparatorBlockEntity extends WorkableClayContainerBlockEntity {

    private static final int OUTPUT_COLUMNS = 4;
    private static final int OUTPUT_ROWS = 4;

    public ChemicalMetalSeparatorBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.CHEMICAL_METAL_SEPARATOR_BLOCK_ENTITY.get(), pos, blockState,
                getValidInputModes(1), getValidOutputModes(OUTPUT_COLUMNS * OUTPUT_ROWS),
                1, OUTPUT_COLUMNS * OUTPUT_ROWS,
                blockEntity -> new WeightedMachineRecipeLogic(blockEntity,
                        ClayiumRecipeTypes.CHEMICAL_METAL_SEPARATOR_RECIPE_TYPE.get(),
                        blockEntity.getEnergyHolder()),
                ClayiumRecipeTypes.CHEMICAL_METAL_SEPARATOR_RECIPE_TYPE.get());
    }

    @Override
    protected UIElement createInputSlots() {
        return new LargeItemSlot()
                .itemSlot(slot -> slot.bind(new ItemHandlerSlot(this.inputItemInventory, 0)));
    }

    @Override
    protected UIElement createOutputSlots() {
        UIElement outputSlotContainer = new UIElement()
                .layout(layout -> layout.flexDirection(FlexDirection.COLUMN));
        for (int row = 0; row < OUTPUT_ROWS; row++) {
            UIElement outputRow = new UIElement().layout(layout -> layout.flexDirection(FlexDirection.ROW));
            for (int column = 0; column < OUTPUT_COLUMNS; column++) {
                int slotIndex = row * OUTPUT_COLUMNS + column;
                outputRow.addChild(new ItemSlot().bind(
                        new ItemHandlerSlot(this.outputItemInventory, slotIndex).setCanPlace(stack -> false)));
            }
            outputSlotContainer.addChild(outputRow);
        }
        return outputSlotContainer;
    }
}
