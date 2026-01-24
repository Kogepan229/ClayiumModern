package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.capability.IClayEnergyHolder;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.client.ldlib.textures.ButtonTextures;
import net.kogepan.clayium.inventory.ClayiumItemStackHandler;
import net.kogepan.clayium.inventory.FilteredItemHandlerModifiable;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.BindableValue;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import org.jetbrains.annotations.NotNull;

/**
 * A trait that provides Clay Energy storage functionality.
 * In Modern, ClayEnergy is handled directly as long.
 * See {@link net.kogepan.clayium.utils.CEUtils} for unit conversion constants.
 */
public class ClayEnergyHolder extends ClayContainerTrait implements IClayEnergyHolder {

    public static final String TRAIT_ID = "clayEnergyHolder";

    private final ClayiumItemStackHandler backingEcSlotHandler;
    private final FilteredItemHandlerModifiable energizedClayItemHandler;

    private long clayEnergy = 0L;
    private int ceSlotStackLimit = 1;

    public ClayEnergyHolder(@NotNull ClayContainerBlockEntity blockEntity) {
        super(blockEntity, TRAIT_ID);
        this.backingEcSlotHandler = new ClayiumItemStackHandler(blockEntity, 1) {

            @Override
            public int getSlotLimit(int slot) {
                return ceSlotStackLimit;
            }
        };
        this.energizedClayItemHandler = new FilteredItemHandlerModifiable(
                this.backingEcSlotHandler,
                stack -> CEUtils.getItemEnergy(stack) > 0);
    }

    @Override
    public void tick() {
        // TODO: Implement energized clay importer when AutoIOTrait pattern is available
        if (blockEntity.getLevel() == null || blockEntity.getLevel().isClientSide()) return;

        Level world = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();
        int limit = 1;

        for (Direction side : Direction.values()) {
            BlockPos neighborPos = pos.relative(side);
            BlockState state = world.getBlockState(neighborPos);
            // TODO: Check for IEnergyStorageUpgradeBlock when available
            // if (state.getBlock() instanceof IEnergyStorageUpgradeBlock upgradeBlock) {
            // limit += upgradeBlock.getExtraStackLimit(world, neighborPos);
            // }
        }

        this.ceSlotStackLimit = limit;
    }

    @Override
    @NotNull
    public IItemHandler getEnergizedClayItemHandler() {
        return this.energizedClayItemHandler;
    }

    @Override
    public long getEnergyStored() {
        return this.clayEnergy;
    }

    @Override
    public boolean drawEnergy(long amount, boolean simulate) {
        if (!hasEnoughEnergy(amount)) return false;
        if (!simulate) {
            this.clayEnergy -= amount;
        }
        return true;
    }

    @Override
    public void addEnergy(long amount) {
        this.clayEnergy += amount;
    }

    /**
     * Tries to consume energized clay from the slot if the current energy is not enough.
     */
    @Override
    public boolean hasEnoughEnergy(long amount) {
        if (this.clayEnergy < amount) {
            tryConsumeEnergizedClay();
        }
        return this.clayEnergy >= amount;
    }

    private void tryConsumeEnergizedClay() {
        ItemStack stack = this.energizedClayItemHandler.getStackInSlot(0);
        if (stack.isEmpty()) return;

        long energy = CEUtils.getItemEnergy(stack);
        if (energy > 0) {
            this.clayEnergy += energy;
            this.energizedClayItemHandler.extractItem(0, 1, false);
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        tag.putLong("clayEnergy", clayEnergy);
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        if (tag.contains("clayEnergy")) {
            clayEnergy = tag.getLong("clayEnergy");
        }
    }

    private void onClickEnergyButton() {
        this.addEnergy(1);
    }

    private String getFormattedEnergyText() {
        return CEUtils.formatCE(this.clayEnergy);
    }

    public CLabel createEnergyTextUIElement() {
        CLabel label = new CLabel();
        label
                .bindDataSource(SupplierDataSource.of(() -> Component.literal(getFormattedEnergyText())))
                .addChild(new BindableValue<Long>().bind(DataBindingBuilder.longValS2C(() -> this.clayEnergy)
                        .remoteSetter((energy) -> this.clayEnergy = energy).build()));
        return label;
    }

    public UIElement createEnergyButtonElement() {
        return new Button()
                .noText()
                .buttonStyle(style -> style
                        .baseTexture(ButtonTextures.CE_BUTTON.base)
                        .hoverTexture(ButtonTextures.CE_BUTTON.hovered)
                        .pressedTexture(ButtonTextures.CE_BUTTON.hovered))
                .layout(l -> l.width(16).height(16))
                .addEventListener(UIEvents.CLICK, e -> {
                    // this.onClickEnergyButton();
                })
                .addServerEventListener(UIEvents.CLICK, e -> {
                    this.onClickEnergyButton();
                });
    }
}
