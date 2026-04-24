package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.trait.AutoIOTrait;
import net.kogepan.clayium.blockentities.trait.ClayEnergyHolder;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.inventory.ClayiumItemStackHandler;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.utils.CEUtils;
import net.kogepan.clayium.utils.MachineIOMode;
import net.kogepan.clayium.utils.ProgressionRates;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.slot.ItemHandlerSlot;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.FlexDirection;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SaltExtractorBlockEntity extends ClayContainerBlockEntity {

    /** Progress units per one salt dust. */
    public static final int PROGRESS_MAX = 100;

    /** CE consumed per tick when producing (energyPerWork = 30 in Original). */
    public static final long ENERGY_PER_WORK = CEUtils.TEN_MICRO_CE * 30;

    /**
     * Base efficiency (progress per tick) per tier (tiers 4–7).
     * Matches TileCobblestoneGenerator.EFFICIENCES[4..7].
     */
    public static final int[] EFFICIENCIES = new int[] { 50, 200, 1000, 8000 };

    /** Slot layout matches CobblestoneGenerator for tier 4–7: 4→2x1, 5→3x2, 6→4x3, 7→5x4. */
    private static int inventoryRowSizeForTier(int tier) {
        return switch (tier) {
            case 4 -> 1;
            case 5 -> 2;
            case 6 -> 3;
            case 7 -> 4;
            default -> 1;
        };
    }

    private static int inventoryColumnSizeForTier(int tier) {
        return switch (tier) {
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
    private final long cePerTick;
    @Getter
    private final ClayEnergyHolder energyHolder;

    private int progress;
    private int waterCount;

    public SaltExtractorBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.SALT_EXTRACTOR_BLOCK_ENTITY.get(), pos, blockState,
                List.of(MachineIOMode.NONE, MachineIOMode.CE), List.of(MachineIOMode.NONE, MachineIOMode.ALL));

        this.inventoryRowSize = inventoryRowSizeForTier(this.tier);
        this.inventoryColumnSize = inventoryColumnSizeForTier(this.tier);
        int efficiencyIndex = this.tier - 4;
        this.progressEfficiency = ProgressionRates.multiplyInt(
                EFFICIENCIES[Math.max(0, Math.min(efficiencyIndex, EFFICIENCIES.length - 1))]);
        this.cePerTick = this.progressEfficiency * ENERGY_PER_WORK;
        this.outputInventory = new ClayiumItemStackHandler(this,
                this.inventoryRowSize * this.inventoryColumnSize);
        this.energyHolder = new ClayEnergyHolder(this);

        this.addTrait(new AutoIOTrait.Expoter(this, this.tier, false));
        this.addTrait(this.energyHolder);
    }

    @Override
    public void initDefaultRoutes() {
        BlockState blockState = this.getBlockState();
        if (blockState.getBlock() instanceof ClayContainerBlock containerBlock) {
            Direction facing = blockState.getValue(containerBlock.getFacingProperty());
            this.inputModes.setMode(facing.getOpposite(), MachineIOMode.CE);
            this.outputModes.setMode(facing, MachineIOMode.ALL);
        }
    }

    @Override
    protected void tick() {
        super.tick();
        Level level = this.level;
        if (level == null || level.isClientSide()) {
            return;
        }

        this.waterCount = countWater();

        if (this.waterCount > 0 && this.energyHolder.drawEnergy(this.cePerTick, false)) {
            this.progress += this.progressEfficiency * this.waterCount;
            setChanged();
        }

        while (this.progress >= PROGRESS_MAX) {
            this.progress -= PROGRESS_MAX;
            ItemStack salt = ClayiumItems.SALT_DUST.toStack();
            ItemStack remainder = ItemHandlerHelper.insertItemStacked(this.outputInventory, salt, false);
            if (!remainder.isEmpty()) {
                this.progress = Math.min(this.progress, PROGRESS_MAX);
                break;
            }
            setChanged();
        }
    }

    /**
     * Counts water blocks adjacent in all 6 directions (N, S, E, W, UP, DOWN).
     */
    private int countWater() {
        Level level = this.level;
        if (level == null) {
            return 0;
        }
        int count = 0;
        for (Direction direction : Direction.values()) {
            FluidState fluidState = level.getFluidState(this.worldPosition.relative(direction));
            if (fluidState.is(FluidTags.WATER)) {
                count++;
            }
        }
        return count;
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
        UIElement slotsElement = new UIElement().layout(layout -> layout.justifyContent(AlignContent.CENTER));
        root.addChild(slotsElement);
        for (int row = 0; row < this.inventoryRowSize; row++) {
            UIElement rowElement = new UIElement().layout(
                    layout -> layout.flexDirection(FlexDirection.ROW).justifyContent(AlignContent.CENTER));
            slotsElement.addChild(rowElement);
            for (int col = 0; col < this.inventoryColumnSize; col++) {
                int slotIndex = col + row * this.inventoryColumnSize;
                rowElement.addChild(new ItemSlot().bind(new ItemHandlerSlot(this.outputInventory, slotIndex)));
            }
        }

        root.addChild(this.energyHolder.createEnergyTextUIElement().textStyle(style -> style.adaptiveWidth(true)));
    }
}
