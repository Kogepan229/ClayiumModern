package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.api.block.IOverclockerBlock;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.client.ldlib.elements.CLabel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.BindableValue;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/** Calculates and applies acceleration from the six blocks adjacent to a machine. */
public class OverclockHandler extends ClayContainerTrait {

    public static final String TRAIT_ID = "overclockHandler";
    public static final int MAX_VIRTUAL_TICKS = 10;

    private static final String TAG_OPERATION_ACCUMULATOR = "operationAccumulator";

    private double totalFactor = 1.0D;
    private double accelerationFactor = 1.0D;
    private double compensationFactor = 1.0D;
    private double operationAccumulator;
    private int operationsThisTick = 1;

    public OverclockHandler(@NotNull ClayContainerBlockEntity blockEntity) {
        super(blockEntity, TRAIT_ID);
    }

    @Override
    public void tick() {
        Level level = this.blockEntity.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        this.operationAccumulator += this.accelerationFactor;
        int operations = (int) Math.floor(this.operationAccumulator);
        this.operationsThisTick = Math.max(1, Math.min(MAX_VIRTUAL_TICKS, operations));
        this.operationAccumulator -= this.operationsThisTick;
        if (!Double.isFinite(this.operationAccumulator) || this.operationAccumulator < 0.0D) {
            this.operationAccumulator = 0.0D;
        }
    }

    @Override
    public void onLoad() {
        this.refreshOverclockFactor();
    }

    /** Recalculates the multiplier from all six adjacent blocks. */
    public void refreshOverclockFactor() {
        Level level = this.blockEntity.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockPos machinePos = this.blockEntity.getBlockPos();
        double factor = 1.0D;
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = machinePos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (!(neighborState.getBlock() instanceof IOverclockerBlock overclocker)) {
                continue;
            }
            double neighborFactor = sanitizeFactor(
                    overclocker.getOverclockFactor(neighborState, level, neighborPos));
            if (factor > Double.MAX_VALUE / neighborFactor) {
                factor = Double.MAX_VALUE;
            } else {
                factor *= neighborFactor;
            }
        }

        this.totalFactor = factor;
        this.accelerationFactor = Math.min(factor, MAX_VIRTUAL_TICKS);
        this.compensationFactor = factor > MAX_VIRTUAL_TICKS ? factor / MAX_VIRTUAL_TICKS : 1.0D;
    }

    /** Returns {@code 1.0} for values outside the public Overclocker contract. */
    public static double sanitizeFactor(double factor) {
        return Double.isFinite(factor) && factor >= 1.0D ? factor : 1.0D;
    }

    public double getTotalFactor() {
        return this.totalFactor;
    }

    public double getCompensationFactor() {
        return this.compensationFactor;
    }

    public int getOperationsThisTick() {
        return this.operationsThisTick;
    }

    /** Samples the current compensation and converts a base recipe duration. */
    public long applyDuration(long baseDuration) {
        long safeDuration = Math.max(1L, baseDuration);
        if (this.compensationFactor <= 1.0D) {
            return safeDuration;
        }
        return Math.max(1L, (long) (safeDuration / this.compensationFactor));
    }

    /** Samples the current compensation and converts a base CE cost per virtual tick. */
    public long applyCEPerTick(long baseCEPerTick) {
        if (baseCEPerTick <= 0L || this.compensationFactor <= 1.0D) {
            return Math.max(0L, baseCEPerTick);
        }
        double effective = baseCEPerTick * Math.pow(this.compensationFactor, 1.5D);
        if (!Double.isFinite(effective) || effective >= Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        }
        return Math.max(baseCEPerTick, (long) effective);
    }

    /** Creates the server-to-client bound multiplier label shared by supported machine UIs. */
    public UIElement createFactorUIElement() {
        CLabel label = new CLabel();
        label.bind(DataBindingBuilder.componentS2C(() -> Component.translatable(
                "gui.clayium.overclock",
                String.format(Locale.ROOT, "%.3g", this.totalFactor))).build());
        label.addChild(new BindableValue<Boolean>().bind(DataBindingBuilder.boolS2C(() -> this.totalFactor != 1.0D)
                .remoteSetter(label::setDisplay)
                .build()));
        return label;
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        tag.putDouble(TAG_OPERATION_ACCUMULATOR, this.operationAccumulator);
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        double accumulator = tag.getDouble(TAG_OPERATION_ACCUMULATOR);
        this.operationAccumulator = Double.isFinite(accumulator) && accumulator >= 0.0D ?
                accumulator % 1.0D : 0.0D;
    }
}
