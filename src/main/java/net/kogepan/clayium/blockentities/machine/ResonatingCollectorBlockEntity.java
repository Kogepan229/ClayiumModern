package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.api.configuration.MachineIOMode;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.trait.AutoIOTrait;
import net.kogepan.clayium.blockentities.trait.OverclockHandler;
import net.kogepan.clayium.blockentities.trait.ResonanceManager;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.inventory.ClayiumItemStackHandler;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.utils.ProgressionRates;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.slot.ItemHandlerSlot;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.FlexDirection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Produces Antimatter from resonance without consuming Clay Energy. */
public class ResonatingCollectorBlockEntity extends ClayContainerBlockEntity {

    public static final long BASE_DURATION = 10_000L;
    private static final int INVENTORY_SIZE = 9;

    private final ClayiumItemStackHandler outputInventory;
    private final OverclockHandler overclockHandler;
    private final ResonanceManager resonanceManager;

    private long progress;

    public ResonatingCollectorBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.RESONATING_COLLECTOR_BLOCK_ENTITY.get(), pos, blockState,
                List.of(MachineIOMode.NONE), List.of(MachineIOMode.NONE, MachineIOMode.ALL));
        this.outputInventory = new ClayiumItemStackHandler(this, INVENTORY_SIZE);
        this.overclockHandler = new OverclockHandler(this);
        this.resonanceManager = new ResonanceManager(this);
        this.addTrait(this.overclockHandler);
        this.addTrait(this.resonanceManager);
        this.addTrait(new AutoIOTrait.Expoter(this, this.tier, false));
    }

    @Override
    public void initDefaultRoutes() {
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof ClayContainerBlock containerBlock) {
            this.outputModes.setMode(state.getValue(containerBlock.getFacingProperty()), MachineIOMode.ALL);
        }
    }

    @Override
    protected void tick() {
        super.tick();
        Level level = this.level;
        if (level == null || level.isClientSide()) {
            return;
        }

        long increment = (long) Math.floor(ProgressionRates.multiplyDouble(
                this.resonanceManager.getResonance() - 1.0D));
        int operations = this.overclockHandler.getOperationsThisTick();
        long effectiveDuration = this.overclockHandler.applyDuration(BASE_DURATION);
        long maximumProgress = multiplySaturated(effectiveDuration, INVENTORY_SIZE * 64L);
        long previousProgress = this.progress;
        if (increment > 0L) {
            long added = multiplySaturated(increment, operations);
            this.progress = addSaturated(this.progress, added);
        }
        this.progress = Math.min(maximumProgress, this.progress);

        while (this.progress >= effectiveDuration) {
            int requested = (int) Math.min(64L, this.progress / effectiveDuration);
            ItemStack stack = new ItemStack(ClayiumItems.ANTIMATTER_MATTER.get(), requested);
            ItemStack remainder = ItemHandlerHelper.insertItemStacked(this.outputInventory, stack, false);
            int inserted = requested - remainder.getCount();
            if (inserted <= 0) {
                break;
            }
            this.progress -= effectiveDuration * inserted;
        }
        if (this.progress != previousProgress) {
            this.setChanged();
        }
    }

    private static long addSaturated(long left, long right) {
        return left > Long.MAX_VALUE - right ? Long.MAX_VALUE : left + right;
    }

    private static long multiplySaturated(long left, long right) {
        return left > 0L && right > Long.MAX_VALUE / left ? Long.MAX_VALUE : left * right;
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
        tag.putLong("progress", this.progress);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("outputInventory")) {
            this.outputInventory.deserializeNBT(provider, tag.getCompound("outputInventory"));
        }
        this.progress = Math.max(0L, tag.getLong("progress"));
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        root.addChild(new CLabel().bind(DataBindingBuilder.componentS2C(() -> Component.translatable(
                "gui.clayium.resonating_collector.progress", this.progress,
                this.overclockHandler.applyDuration(BASE_DURATION))).build()));
        root.addChild(this.resonanceManager.createResonanceUIElement());
        root.addChild(this.overclockHandler.createFactorUIElement());
        UIElement slots = new UIElement().layout(layout -> layout.justifyContent(AlignContent.CENTER));
        root.addChild(slots);
        for (int row = 0; row < 3; row++) {
            UIElement rowElement = new UIElement().layout(layout -> layout.flexDirection(FlexDirection.ROW)
                    .justifyContent(AlignContent.CENTER));
            slots.addChild(rowElement);
            for (int column = 0; column < 3; column++) {
                int index = row * 3 + column;
                rowElement.addChild(new ItemSlot().bind(
                        new ItemHandlerSlot(this.outputInventory, index).setCanPlace(stack -> false)));
            }
        }
    }
}
