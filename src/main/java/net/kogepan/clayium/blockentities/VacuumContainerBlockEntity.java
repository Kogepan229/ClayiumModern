package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.client.ldlib.elements.LargeItemSlot;
import net.kogepan.clayium.client.ldlib.elements.PhantomItemSlot;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.slot.ItemHandlerSlot;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import dev.vfyjxf.taffy.style.TaffyPosition;
import org.jetbrains.annotations.NotNull;

public class VacuumContainerBlockEntity extends AbstractFilteredContainerBlockEntity {

    private static final int MACHINE_UI_WIDTH = 164;
    private static final int MACHINE_UI_HEIGHT = 49;

    private final VoidItemHandler voidItemHandler = new VoidItemHandler();

    public VacuumContainerBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.VACUUM_CONTAINER_BLOCK_ENTITY.get(), pos, blockState);
    }

    @NotNull
    public ItemStack getDisplayedItem() {
        return this.getGlobalFilter();
    }

    @Override
    public IItemHandlerModifiable getInputInventory() {
        return this.voidItemHandler;
    }

    @Override
    public IItemHandlerModifiable getOutputInventory() {
        return this.voidItemHandler;
    }

    @Override
    protected IItemHandler getPassiveItemHandler() {
        return this.voidItemHandler;
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        UIElement machineSection = new UIElement()
                .layout(layout -> layout.width(MACHINE_UI_WIDTH).height(MACHINE_UI_HEIGHT));

        machineSection.addChild(new LargeItemSlot()
                .itemSlot(slot -> slot
                        .bind(new ItemHandlerSlot(this.voidItemHandler, 0).setCanTake(player -> false)))
                .layout(layout -> layout
                        .positionType(TaffyPosition.ABSOLUTE)
                        .left(70)
                        .top(12)));
        machineSection.addChild(PhantomItemSlot.create(this.getGlobalFilterInventory(), 0)
                .layout(layout -> layout
                        .positionType(TaffyPosition.ABSOLUTE)
                        .left(135)
                        .top(-2)));

        root.addChild(machineSection);
    }

    private boolean matchesGlobalFilter(@NotNull ItemStack stack) {
        ItemStack filterStack = this.getGlobalFilter();
        if (filterStack.isEmpty()) {
            return true;
        }
        if (ItemStack.isSameItemSameComponents(filterStack, stack)) {
            return true;
        }
        ItemFilterData filter = filterStack.getCapability(ClayiumCapabilities.ITEM_FILTER_DATA);
        return filter != null && filter.test(stack);
    }

    private static void validateSlot(int slot) {
        if (slot != 0) {
            throw new IllegalArgumentException("Vacuum Container only has slot 0, got " + slot);
        }
    }

    private final class VoidItemHandler implements IItemHandlerModifiable {

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        @NotNull
        public ItemStack getStackInSlot(int slot) {
            validateSlot(slot);
            return ItemStack.EMPTY;
        }

        @Override
        @NotNull
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            validateSlot(slot);
            return stack.isEmpty() || !this.isItemValid(slot, stack) ? stack : ItemStack.EMPTY;
        }

        @Override
        @NotNull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            validateSlot(slot);
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            validateSlot(slot);
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            validateSlot(slot);
            return !stack.isEmpty() && matchesGlobalFilter(stack);
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            validateSlot(slot);
        }
    }
}
