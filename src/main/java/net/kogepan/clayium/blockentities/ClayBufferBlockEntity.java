package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.blockentities.trait.AutoIOTrait;
import net.kogepan.clayium.blockentities.trait.ItemFilterHolderTrait;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.capability.IItemFilter;
import net.kogepan.clayium.capability.IItemFilterApplicatable;
import net.kogepan.clayium.inventory.ClayiumItemStackHandler;
import net.kogepan.clayium.inventory.FilteredItemHandler;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.slot.ItemHandlerSlot;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import org.appliedenergistics.yoga.YogaFlexDirection;
import org.appliedenergistics.yoga.YogaJustify;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClayBufferBlockEntity extends ClayContainerBlockEntity {

    private final int inventoryRowSize;
    private final int inventoryColumnSize;
    private final ClayiumItemStackHandler itemInventory;

    public ClayBufferBlockEntity(BlockPos pos, BlockState blockState) {
        super(ClayiumBlockEntityTypes.CLAY_BUFFER_BLOCK_ENTITY.get(), pos, blockState,
                List.of(MachineIOMode.NONE, MachineIOMode.ALL), List.of(MachineIOMode.NONE, MachineIOMode.ALL));

        this.inventoryRowSize = switch (tier) {
            case 4, 5, 6, 7 -> tier - 3;
            case 8 -> 4;
            case 9, 10, 11, 12, 13 -> 6;
            default -> throw new IllegalStateException("Invalid tier value: " + tier);
        };
        this.inventoryColumnSize = switch (tier) {
            case 4, 5, 6, 7 -> tier - 2;
            case 8, 9, 10, 11, 12, 13 -> 9;
            default -> throw new IllegalStateException("Invalid tier value: " + tier);
        };

        this.itemInventory = new ClayiumItemStackHandler(this, this.inventoryRowSize * this.inventoryColumnSize);

        this.addTrait(new AutoIOTrait.Combined(this, 4, true));
    }

    @Override
    public void initDefaultRoutes() {
        BlockState blockState = this.getBlockState();
        if (blockState.getBlock() instanceof ClayContainerBlock containerBlock) {
            this.inputModes.setMode(blockState.getValue(containerBlock.getFacingProperty()).getOpposite(),
                    MachineIOMode.ALL);
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
        var trait = getTrait(ItemFilterHolderTrait.TRAIT_ID);
        IItemFilter filter = trait instanceof IItemFilterApplicatable a ? a.getFilter(side) : null;
        if (filter != null) {
            return new FilteredItemHandler(this.itemInventory, filter);
        }
        return this.itemInventory;
    }

    @Override
    protected void invalidateItemHandlerCapability() {}

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
        UIElement slotsElement = new UIElement().layout(layout -> layout.setJustifyContent(YogaJustify.CENTER));
        root.addChild(slotsElement);
        for (int row = 0; row < this.inventoryRowSize; row++) {
            UIElement rowElement = new UIElement().layout(
                    layout -> layout.flexDirection(YogaFlexDirection.ROW).setJustifyContent(YogaJustify.CENTER));
            slotsElement.addChild(rowElement);
            for (int col = 0; col < this.inventoryColumnSize; col++) {
                rowElement.addChild(new ItemSlot()
                        .bind(new ItemHandlerSlot(this.itemInventory, col + row * this.inventoryColumnSize)));
            }
        }
    }
}
