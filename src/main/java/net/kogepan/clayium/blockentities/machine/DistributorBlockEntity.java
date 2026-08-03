package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.api.configuration.MachineIOMode;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.trait.DistributorAutoIOTrait;
import net.kogepan.clayium.blockentities.trait.ItemFilterHolderTrait;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.capability.IItemFilterApplicatable;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.inventory.ClayiumItemStackHandler;
import net.kogepan.clayium.inventory.FilteredItemHandler;
import net.kogepan.clayium.inventory.MachineIOInventoryWrapper;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.slot.ItemHandlerSlot;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.FlexDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.IntSupplier;

public class DistributorBlockEntity extends ClayContainerBlockEntity {

    private final int groupColumns;
    private final int groupRows;
    private final int groupCount;
    private final ClayiumItemStackHandler itemInventory;

    public DistributorBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.DISTRIBUTOR_BLOCK_ENTITY.get(), pos, blockState,
                List.of(MachineIOMode.NONE, MachineIOMode.ALL), List.of(MachineIOMode.NONE, MachineIOMode.ALL));

        this.groupColumns = switch (this.tier) {
            case 7 -> 2;
            case 8 -> 3;
            case 9 -> 4;
            default -> throw new IllegalStateException("Invalid distributor tier: " + this.tier);
        };
        this.groupRows = switch (this.tier) {
            case 7, 8 -> 2;
            case 9 -> 3;
            default -> throw new IllegalStateException("Invalid distributor tier: " + this.tier);
        };
        this.groupCount = this.groupColumns * this.groupRows;
        this.itemInventory = new ClayiumItemStackHandler(this, this.groupCount * 4);

        int maxIo = switch (this.tier) {
            case 7 -> 64;
            case 8 -> 128;
            case 9 -> 512;
            default -> throw new IllegalStateException("Invalid distributor tier: " + this.tier);
        };
        this.addTrait(new DistributorAutoIOTrait(this, maxIo, maxIo, 1));
    }

    @Override
    public void initDefaultRoutes() {
        BlockState blockState = this.getBlockState();
        if (blockState.getBlock() instanceof ClayContainerBlock containerBlock) {
            Direction opposite = blockState.getValue(containerBlock.getFacingProperty()).getOpposite();
            this.inputModes.setMode(opposite, MachineIOMode.ALL);
            for (Direction direction : Direction.values()) {
                if (direction != opposite) {
                    this.outputModes.setMode(direction, MachineIOMode.ALL);
                }
            }
        }
    }

    @Override
    public IItemHandlerModifiable getInputInventory() {
        return this.itemInventory;
    }

    @Override
    public IItemHandlerModifiable getOutputInventory() {
        return this.itemInventory;
    }

    @Override
    @Nullable
    public IItemHandler getExposedItemHandler(@Nullable Direction side) {
        if (side == null) {
            return null;
        }
        var ioTrait = getTrait(DistributorAutoIOTrait.TRAIT_ID);
        if (!(ioTrait instanceof DistributorAutoIOTrait distributorTrait)) {
            return null;
        }

        IItemHandler inputInventory = this.inputModes.getMode(side) == MachineIOMode.ALL ?
                new DynamicGroupItemHandler(this, distributorTrait::getImportPtr) : null;
        IItemHandler outputInventory = this.outputModes.getMode(side) == MachineIOMode.ALL ?
                new DynamicGroupItemHandler(this, distributorTrait::getExportPtr) : null;

        var trait = getTrait(ItemFilterHolderTrait.TRAIT_ID);
        ItemFilterData filter = trait instanceof IItemFilterApplicatable applicatable ? applicatable.getFilter(side) :
                null;
        if (filter != null) {
            inputInventory = inputInventory != null ? new FilteredItemHandler(inputInventory, filter) : null;
            outputInventory = outputInventory != null ? new FilteredItemHandler(outputInventory, filter) : null;
        }
        return new MachineIOInventoryWrapper(inputInventory, outputInventory);
    }

    public IItemHandler getGroupHandler(int groupIndex) {
        int normalized = Math.floorMod(groupIndex, this.groupCount);
        int start = normalized * 4;
        return new RangedWrapper(this.itemInventory, start, start + 4);
    }

    @Nullable
    public IItemHandler getFilteredOutputGroupHandler(@NotNull Direction side) {
        if (this.outputModes.getMode(side) != MachineIOMode.ALL) {
            return null;
        }
        var ioTrait = getTrait(DistributorAutoIOTrait.TRAIT_ID);
        if (!(ioTrait instanceof DistributorAutoIOTrait distributorTrait)) {
            return null;
        }
        IItemHandler outputInventory = this.getGroupHandler(distributorTrait.getExportPtr());
        var trait = getTrait(ItemFilterHolderTrait.TRAIT_ID);
        ItemFilterData filter = trait instanceof IItemFilterApplicatable applicatable ? applicatable.getFilter(side) :
                null;
        return filter != null ? new FilteredItemHandler(outputInventory, filter) : outputInventory;
    }

    public int getGroupCount() {
        return this.groupCount;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("itemInventory", this.itemInventory.serializeNBT(provider));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("itemInventory")) {
            this.itemInventory.deserializeNBT(provider, tag.getCompound("itemInventory"));
        }
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        UIElement groupsElement = new UIElement().layout(
                layout -> layout.justifyContent(AlignContent.CENTER).gapAll(2));
        root.addChild(groupsElement);
        for (int groupRow = 0; groupRow < this.groupRows; groupRow++) {
            UIElement groupRowElement = new UIElement().layout(
                    layout -> layout.flexDirection(FlexDirection.ROW).justifyContent(AlignContent.CENTER).gapAll(2));
            groupsElement.addChild(groupRowElement);
            for (int groupCol = 0; groupCol < this.groupColumns; groupCol++) {
                int base = (groupRow * this.groupColumns + groupCol) * 4;
                UIElement groupElement = new UIElement().layout(layout -> layout.gapAll(0));
                UIElement top = new UIElement().layout(layout -> layout.flexDirection(FlexDirection.ROW));
                UIElement bottom = new UIElement().layout(layout -> layout.flexDirection(FlexDirection.ROW));
                top.addChild(new ItemSlot().bind(new ItemHandlerSlot(this.itemInventory, base)));
                top.addChild(new ItemSlot().bind(new ItemHandlerSlot(this.itemInventory, base + 1)));
                bottom.addChild(new ItemSlot().bind(new ItemHandlerSlot(this.itemInventory, base + 2)));
                bottom.addChild(new ItemSlot().bind(new ItemHandlerSlot(this.itemInventory, base + 3)));
                groupElement.addChild(top);
                groupElement.addChild(bottom);
                groupRowElement.addChild(groupElement);
            }
        }
    }

    private static final class DynamicGroupItemHandler implements IItemHandler {

        private final DistributorBlockEntity distributor;
        private final IntSupplier groupIndexSupplier;

        private DynamicGroupItemHandler(DistributorBlockEntity distributor, IntSupplier groupIndexSupplier) {
            this.distributor = distributor;
            this.groupIndexSupplier = groupIndexSupplier;
        }

        private IItemHandler current() {
            return this.distributor.getGroupHandler(this.groupIndexSupplier.getAsInt());
        }

        @Override
        public int getSlots() {
            return this.current().getSlots();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return this.current().getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return this.current().insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return this.current().extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return this.current().getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return this.current().isItemValid(slot, stack);
        }
    }
}
