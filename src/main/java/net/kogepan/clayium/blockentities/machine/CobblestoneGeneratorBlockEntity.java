package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
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
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.slot.ItemHandlerSlot;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.BindableValue;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import org.appliedenergistics.yoga.YogaFlexDirection;
import org.appliedenergistics.yoga.YogaJustify;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CobblestoneGeneratorBlockEntity extends ClayContainerBlockEntity {

    /** Progress units per one cobblestone. */
    public static final int PROGRESS_MAX = 100;

    /**
     * Base efficiency (progress per tick) per tier(tiers 1–7).
     */
    public static final int[] EFFICIENCIES = new int[] { 2, 5, 15, 50, 200, 1000, 8000 };

    /** Slot layout matches Original TileClayBuffer: tier 1–3 → 1×1, 4 → 2×1, 5 → 3×2, 6 → 4×3, 7 → 5×4. */
    private static int inventoryRowSizeForTier(int tier) {
        return switch (tier) {
            case 1, 2, 3 -> 1;
            case 4 -> 1;
            case 5 -> 2;
            case 6 -> 3;
            case 7 -> 4;
            default -> 1;
        };
    }

    private static int inventoryColumnSizeForTier(int tier) {
        return switch (tier) {
            case 1, 2, 3 -> 1;
            case 4 -> 2;
            case 5 -> 3;
            case 6 -> 4;
            case 7 -> 5;
            default -> 1;
        };
    }

    private final int inventoryRowSize;
    private final int inventoryColumnSize;
    private final ClayiumItemStackHandler outputInventory;
    private final int progressEfficiency;

    private int progress;

    public CobblestoneGeneratorBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.COBBLESTONE_GENERATOR_BLOCK_ENTITY.get(), pos, blockState,
                List.of(MachineIOMode.NONE), List.of(MachineIOMode.NONE, MachineIOMode.ALL));

        this.inventoryRowSize = inventoryRowSizeForTier(this.tier);
        this.inventoryColumnSize = inventoryColumnSizeForTier(this.tier);
        this.progressEfficiency = EFFICIENCIES[this.tier - 1];
        this.outputInventory = new ClayiumItemStackHandler(this,
                this.inventoryRowSize * this.inventoryColumnSize);

        this.addTrait(new AutoIOTrait.Expoter(this, this.tier, false));
    }

    @Override
    public void initDefaultRoutes() {
        BlockState blockState = this.getBlockState();
        if (blockState.getBlock() instanceof ClayContainerBlock containerBlock) {
            this.outputModes.setMode(blockState.getValue(containerBlock.getFacingProperty()),
                    MachineIOMode.ALL);
        }
    }

    @Override
    protected void tick() {
        super.tick();
        Level level = this.level;
        if (level == null || level.isClientSide()) {
            return;
        }

        if (canProduce()) {
            progress += progressEfficiency;
            setChanged();
        }

        while (progress >= PROGRESS_MAX) {
            progress -= PROGRESS_MAX;
            ItemStack cobble = new ItemStack(Items.COBBLESTONE);
            ItemStack remainder = ItemHandlerHelper.insertItemStacked(this.outputInventory, cobble, false);
            if (!remainder.isEmpty()) {
                progress = Math.min(progress, PROGRESS_MAX);
                break;
            }
            setChanged();
        }
    }

    /**
     * Returns true when at least one horizontal neighbor is water and at least one is lava
     */
    private boolean canProduce() {
        Level level = this.level;
        if (level == null) {
            return false;
        }
        boolean hasWater = false;
        boolean hasLava = false;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = this.worldPosition.relative(direction);
            FluidState fluidState = level.getFluidState(neighborPos);
            if (fluidState.is(FluidTags.WATER)) {
                hasWater = true;
            }
            if (fluidState.is(FluidTags.LAVA)) {
                hasLava = true;
            }
            if (hasWater && hasLava) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IItemHandlerModifiable getInputInventory() {
        return (IItemHandlerModifiable) EmptyItemHandler.INSTANCE;
    }

    @Override
    public IItemHandlerModifiable getOutputInventory() {
        return this.outputInventory;
    }

    @Override
    protected void invalidateItemHandlerCapability() {}

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("outputInventory", this.outputInventory.serializeNBT(provider));
        tag.putInt("progress", this.progress);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("outputInventory")) {
            this.outputInventory.deserializeNBT(provider, tag.getCompound("outputInventory"));
        }
        if (tag.contains("progress")) {
            this.progress = tag.getInt("progress");
        }
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        root.addChild(new BindableValue<Integer>().bind(DataBindingBuilder.intValS2C(() -> this.progress)
                .remoteSetter((val) -> this.progress = val).build()));
        root.addChild(new UIElement()
                .addChild(new CLabel().bindDataSource(SupplierDataSource
                        .of(() -> Component.translatable("gui.clayium.cobblestone_generator.progress",
                                this.progress)))));
        UIElement slotsElement = new UIElement().layout(layout -> layout.setJustifyContent(YogaJustify.CENTER));
        root.addChild(slotsElement);
        for (int row = 0; row < this.inventoryRowSize; row++) {
            UIElement rowElement = new UIElement().layout(
                    layout -> layout.flexDirection(YogaFlexDirection.ROW).setJustifyContent(YogaJustify.CENTER));
            slotsElement.addChild(rowElement);
            for (int col = 0; col < this.inventoryColumnSize; col++) {
                int slotIndex = col + row * this.inventoryColumnSize;
                rowElement.addChild(new ItemSlot().bind(new ItemHandlerSlot(this.outputInventory, slotIndex)));
            }
        }
    }
}
