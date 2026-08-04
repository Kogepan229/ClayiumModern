package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.api.block.IResonatingBlock;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.client.ldlib.elements.CLabel;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/** Caches the combined resonance in the loaded 5x5x5 area around a CA machine. */
public class ResonanceManager extends ClayContainerTrait {

    public static final String TRAIT_ID = "resonanceManager";
    public static final int SCAN_INTERVAL = 20;
    public static final int RANGE = 2;

    private static final double MAX_RESONANCE = Long.MAX_VALUE;

    private double resonance = 1.0D;
    private int ticksUntilScan;

    public ResonanceManager(@NotNull ClayContainerBlockEntity blockEntity) {
        super(blockEntity, TRAIT_ID);
    }

    @Override
    public void tick() {
        Level level = this.blockEntity.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }
        if (++this.ticksUntilScan >= SCAN_INTERVAL) {
            this.refreshResonance();
        }
    }

    @Override
    public void onLoad() {
        this.refreshResonance();
    }

    /** Recalculates resonance without loading chunks outside the currently loaded area. */
    public void refreshResonance() {
        Level level = this.blockEntity.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockPos center = this.blockEntity.getBlockPos();
        double product = 1.0D;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-RANGE, -RANGE, -RANGE),
                center.offset(RANGE, RANGE, RANGE))) {
            if (!level.isLoaded(pos)) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof IResonatingBlock resonatingBlock)) {
                continue;
            }
            double factor = resonatingBlock.getResonance(state, level, pos);
            if (!Double.isFinite(factor) || factor < 1.0D) {
                continue;
            }
            if (product >= MAX_RESONANCE / factor) {
                product = MAX_RESONANCE;
                break;
            }
            product *= factor;
        }
        this.resonance = product;
        this.ticksUntilScan = 0;
    }

    public double getResonance() {
        return this.resonance;
    }

    /** Creates a server-to-client bound resonance label for CA machine UIs. */
    public UIElement createResonanceUIElement() {
        return new CLabel().bind(DataBindingBuilder.componentS2C(() -> Component.translatable(
                "gui.clayium.resonance", formatResonance(this.resonance))).build());
    }

    private static String formatResonance(double value) {
        if (value < 10.0D) {
            return String.format(Locale.ROOT, "%.3f", value);
        }
        if (value < 100.0D) {
            return String.format(Locale.ROOT, "%.2f", value);
        }
        if (value < 1000.0D) {
            return String.format(Locale.ROOT, "%.1f", value);
        }
        return String.format(Locale.ROOT, "%.3g", value);
    }
}
