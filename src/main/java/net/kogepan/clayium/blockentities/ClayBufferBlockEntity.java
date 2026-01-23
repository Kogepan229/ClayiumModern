package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.blockentities.trait.AutoIOTrait;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.inventory.ClayiumItemStackHandler;
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
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
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

        int tier;
        if (blockState.getBlock() instanceof ClayContainerBlock containerBlock) {
            tier = containerBlock.tier;
        } else {
            throw new RuntimeException("Invalid block!");
        }

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
        this.inputModes.setMode(this.getBlockState().getValue(ClayContainerBlock.FACING).getOpposite(),
                MachineIOMode.ALL);
    }

    @Override
    protected IItemHandlerModifiable getInputInventory() {
        return this.itemInventory;
    }

    @Override
    protected IItemHandlerModifiable getOutputInventory() {
        return this.itemInventory;
    }

    @Override
    @Nullable
    public IItemHandler getExposedItemHandler(@Nullable Direction side) {
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
        this.itemInventory.deserializeNBT(provider, tag.getCompound("itemInventory"));
    }

    @Override
    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        var root = new UIElement().layout(layout -> layout
                .paddingAll(6)
                .setJustifyContent(YogaJustify.CENTER))
                .addClass("panel_bg");
        root.addChild(
                new CLabel().setText(this.getBlockState().getBlock().getName().getString()));

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

        root.addChild(new CLabel().setText("Inventory"));
        root.addChild(new InventorySlots());
        return new ModularUI(UI.of(root, List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC))),
                holder.player);
    }
}
