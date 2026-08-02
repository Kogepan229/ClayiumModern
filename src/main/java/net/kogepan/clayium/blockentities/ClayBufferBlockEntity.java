package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.inventory.ClayiumItemStorage;
import net.kogepan.clayium.inventory.FilteredItemResourceHandler;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.FlexDirection;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class ClayBufferBlockEntity extends ClayContainerBlockEntity {

    private static final String ITEM_INVENTORY_KEY = "itemInventory";

    private final int inventoryRows;
    private final int inventoryColumns;
    private final ClayiumItemStorage itemInventory;

    public ClayBufferBlockEntity(BlockPos pos, BlockState state) {
        super(
                ClayiumBlockEntityTypes.CLAY_BUFFER_BLOCK_ENTITY.get(),
                pos,
                state,
                List.of(MachineIOMode.NONE, MachineIOMode.ALL),
                List.of(MachineIOMode.NONE, MachineIOMode.ALL));

        this.inventoryRows = switch (this.tier()) {
            case 4, 5, 6, 7 -> this.tier() - 3;
            case 8 -> 4;
            case 9, 10, 11, 12, 13 -> 6;
            default -> throw new IllegalStateException("Invalid Clay Buffer tier: " + this.tier());
        };
        this.inventoryColumns = switch (this.tier()) {
            case 4, 5, 6, 7 -> this.tier() - 2;
            case 8, 9, 10, 11, 12, 13 -> 9;
            default -> throw new IllegalStateException("Invalid Clay Buffer tier: " + this.tier());
        };
        this.itemInventory = new ClayiumItemStorage(
                this.inventoryRows * this.inventoryColumns,
                (index, previousContents) -> this.setChanged());
    }

    public int inventoryRows() {
        return this.inventoryRows;
    }

    public int inventoryColumns() {
        return this.inventoryColumns;
    }

    public ClayiumItemStorage itemInventory() {
        return this.itemInventory;
    }

    @Override
    public void initDefaultRoutes() {
        Direction rear = this.getBlockState().getValue(ClayContainerBlock.FACING).getOpposite();
        this.inputModes.setMode(rear, MachineIOMode.ALL);
    }

    @Override
    public ResourceHandler<ItemResource> getInputInventory() {
        return this.itemInventory;
    }

    @Override
    public ResourceHandler<ItemResource> getOutputInventory() {
        return this.itemInventory;
    }

    @Override
    public @Nullable ResourceHandler<ItemResource> getExposedItemHandler(@Nullable Direction side) {
        if (side == null) {
            return null;
        }
        ItemFilterData filter = this.getFilterForSide(side);
        return filter != null ? new FilteredItemResourceHandler(this.itemInventory, filter) : this.itemInventory;
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        UIElement slots = new UIElement().layout(layout -> layout.justifyContent(AlignContent.CENTER));
        root.addChild(slots);
        for (int row = 0; row < this.inventoryRows; row++) {
            UIElement rowElement = new UIElement().layout(layout -> layout
                    .flexDirection(FlexDirection.ROW)
                    .justifyContent(AlignContent.CENTER));
            slots.addChild(rowElement);
            for (int column = 0; column < this.inventoryColumns; column++) {
                rowElement.addChild(new ItemSlot().bind(
                        this.itemInventory,
                        column + row * this.inventoryColumns));
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild(ITEM_INVENTORY_KEY, this.itemInventory);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.itemInventory.deserialize(input.childOrEmpty(ITEM_INVENTORY_KEY));
    }
}
