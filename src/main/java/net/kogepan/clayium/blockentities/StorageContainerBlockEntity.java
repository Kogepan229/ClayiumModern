package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.client.ldlib.elements.LargeItemSlot;
import net.kogepan.clayium.client.ldlib.elements.PhantomItemSlot;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.slot.ItemHandlerSlot;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class StorageContainerBlockEntity extends AbstractFilteredContainerBlockEntity {

    private static final int MACHINE_UI_WIDTH = 164;
    private static final int MACHINE_UI_HEIGHT = 49;

    public static final int DEFAULT_CAPACITY = 65_536;
    public static final int MAX_CAPACITY = Integer.MAX_VALUE;
    public static final String STORED_ITEM_TAG = "storedItem";
    public static final String STORED_AMOUNT_TAG = "storedAmount";
    public static final String CAPACITY_TAG = "capacity";

    @NotNull
    private ItemStack storedItem = ItemStack.EMPTY;
    private int storedAmount;
    private int capacity = DEFAULT_CAPACITY;
    private boolean storageEffectsPending;
    private boolean clientUpdatePending;
    private boolean packetChangesModelVariant;

    private final StorageItemHandler storageItemHandler = new StorageItemHandler();
    private final GuiInputItemHandler guiInputItemHandler = new GuiInputItemHandler();
    private final GuiOutputItemHandler guiOutputItemHandler = new GuiOutputItemHandler();

    public StorageContainerBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.STORAGE_CONTAINER_BLOCK_ENTITY.get(), pos, blockState);
    }

    @NotNull
    public ItemStack getDisplayedItem() {
        return this.storedItem.isEmpty() ? this.getGlobalFilter() : this.storedItem;
    }

    public int getStoredAmount() {
        return this.storedAmount;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public boolean upgradeToMaximumCapacity() {
        if (this.capacity == MAX_CAPACITY) {
            return false;
        }
        this.capacity = MAX_CAPACITY;
        this.onStorageChanged();
        this.flushStorageChanges();
        return true;
    }

    @Override
    protected void tick() {
        super.tick();
        this.flushStorageChanges();
    }

    @Override
    public IItemHandlerModifiable getInputInventory() {
        return this.storageItemHandler;
    }

    @Override
    public IItemHandlerModifiable getOutputInventory() {
        return this.storageItemHandler;
    }

    @Override
    protected IItemHandler getPassiveItemHandler() {
        return this.storageItemHandler;
    }

    @Override
    @Nullable
    protected String getFrontOverlayVariant() {
        return this.capacity == MAX_CAPACITY ? "upgraded" : null;
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        UIElement machineSection = new UIElement()
                .layout(layout -> layout.width(MACHINE_UI_WIDTH).height(MACHINE_UI_HEIGHT));

        machineSection.addChild(new LargeItemSlot()
                .itemSlot(slot -> slot
                        .bind(new ItemHandlerSlot(this.guiInputItemHandler, 0).setCanTake(player -> false)))
                .layout(layout -> layout
                        .positionType(TaffyPosition.ABSOLUTE)
                        .left(34)
                        .top(12)));
        machineSection.addChild(new LargeItemSlot()
                .itemSlot(slot -> slot.bind(this.createGuiOutputSlot()))
                .layout(layout -> layout
                        .positionType(TaffyPosition.ABSOLUTE)
                        .left(106)
                        .top(12)));
        machineSection.addChild(PhantomItemSlot.create(this.getGlobalFilterInventory(), 0)
                .layout(layout -> layout
                        .positionType(TaffyPosition.ABSOLUTE)
                        .left(135)
                        .top(-2)));

        UIElement amountRow = new UIElement().layout(layout -> layout
                .positionType(TaffyPosition.ABSOLUTE)
                .flexDirection(FlexDirection.ROW)
                .justifyContent(AlignContent.CENTER)
                .width(MACHINE_UI_WIDTH)
                .left(0)
                .top(41));
        amountRow.addChild(new CLabel().bind(DataBindingBuilder.componentS2C(
                () -> Component.literal(this.storedAmount + " / " + this.capacity)).build()));
        machineSection.addChild(amountRow);

        root.addChild(machineSection);
    }

    private ItemHandlerSlot createGuiOutputSlot() {
        return new ItemHandlerSlot(this.guiOutputItemHandler, 0) {

            @Override
            public int getMaxStackSize(@Nonnull ItemStack stack) {
                return Math.min(this.getMaxStackSize(), stack.getMaxStackSize());
            }
        }.setCanPlace(stack -> false);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        this.writeStorageData(tag, provider);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.readStorageData(tag, provider);
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        this.writeStorageData(tag, provider);
        return tag;
    }

    @Override
    protected void onReceivePacket(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        this.packetChangesModelVariant = tag.contains(CAPACITY_TAG) &&
                (tag.getInt(CAPACITY_TAG) == MAX_CAPACITY) != (this.capacity == MAX_CAPACITY);
        this.readStorageData(tag, provider);
        try {
            super.onReceivePacket(tag, provider);
        } finally {
            this.packetChangesModelVariant = false;
        }
    }

    @Override
    protected boolean shouldRefreshModelDataForUpdate(@NotNull CompoundTag tag,
                                                      @NotNull HolderLookup.Provider provider) {
        return this.packetChangesModelVariant || super.shouldRefreshModelDataForUpdate(tag, provider);
    }

    private void writeStorageData(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        tag.put(STORED_ITEM_TAG, this.storedItem.saveOptional(provider));
        tag.putInt(STORED_AMOUNT_TAG, this.storedAmount);
        tag.putInt(CAPACITY_TAG, this.capacity);
    }

    private void readStorageData(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        this.capacity = tag.contains(CAPACITY_TAG) ? Math.max(1, tag.getInt(CAPACITY_TAG)) : DEFAULT_CAPACITY;
        this.storedItem = ItemStack.parseOptional(provider, tag.getCompound(STORED_ITEM_TAG));
        if (!this.storedItem.isEmpty()) {
            this.storedItem.setCount(1);
        }
        this.storedAmount = Math.max(0, tag.getInt(STORED_AMOUNT_TAG));
        this.storedAmount = Math.min(this.storedAmount, this.capacity);
        if (this.storedItem.isEmpty() || this.storedAmount == 0) {
            this.storedItem = ItemStack.EMPTY;
            this.storedAmount = 0;
        }
    }

    private void onStorageChanged() {
        if (!this.storageEffectsPending) {
            this.setChanged();
        }
        this.storageEffectsPending = true;
        this.clientUpdatePending = true;
    }

    private void flushStorageChanges() {
        if (this.storageEffectsPending) {
            this.storageEffectsPending = false;
            this.notifyItemInputInventoryChanged();
            this.notifyItemOutputInventoryChanged();
        }
        if (!this.clientUpdatePending) {
            return;
        }
        this.clientUpdatePending = false;
        this.sendContainerUpdate();
    }

    private static void validateSlot(int slot) {
        if (slot != 0) {
            throw new IllegalArgumentException("Storage Container only has slot 0, got " + slot);
        }
    }

    private final class StorageItemHandler implements IItemHandlerModifiable {

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        @NotNull
        public ItemStack getStackInSlot(int slot) {
            validateSlot(slot);
            if (storedItem.isEmpty() || storedAmount == 0) {
                return ItemStack.EMPTY;
            }
            return storedItem.copyWithCount(Math.min(storedAmount, storedItem.getMaxStackSize()));
        }

        @Override
        @NotNull
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            validateSlot(slot);
            if (!this.canAccept(stack)) {
                return stack;
            }

            int accepted = Math.min(stack.getCount(), capacity - storedAmount);
            if (accepted <= 0) {
                return stack;
            }
            if (!simulate) {
                if (storedItem.isEmpty()) {
                    storedItem = stack.copyWithCount(1);
                }
                storedAmount += accepted;
                onStorageChanged();
            }
            int remainder = stack.getCount() - accepted;
            return remainder == 0 ? ItemStack.EMPTY : stack.copyWithCount(remainder);
        }

        @Override
        @NotNull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            validateSlot(slot);
            if (amount <= 0 || storedItem.isEmpty() || storedAmount == 0) {
                return ItemStack.EMPTY;
            }

            int extracted = Math.min(Math.min(amount, storedAmount), storedItem.getMaxStackSize());
            ItemStack result = storedItem.copyWithCount(extracted);
            if (!simulate) {
                storedAmount -= extracted;
                if (storedAmount == 0) {
                    storedItem = ItemStack.EMPTY;
                }
                onStorageChanged();
            }
            return result;
        }

        @Override
        public int getSlotLimit(int slot) {
            validateSlot(slot);
            return capacity;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            validateSlot(slot);
            return !stack.isEmpty() && matchesExactGlobalFilter(stack);
        }

        private boolean canAccept(@NotNull ItemStack stack) {
            return this.isItemValid(0, stack) &&
                    (storedItem.isEmpty() || ItemStack.isSameItemSameComponents(storedItem, stack));
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            validateSlot(slot);
            ItemStack current = this.getStackInSlot(slot);
            if (current.isEmpty()) {
                this.insertItem(slot, stack, false);
                return;
            }
            if (stack.isEmpty()) {
                this.extractItem(slot, current.getCount(), false);
                return;
            }
            if (!ItemStack.isSameItemSameComponents(current, stack)) {
                return;
            }
            int difference = stack.getCount() - current.getCount();
            if (difference > 0) {
                this.insertItem(slot, stack.copyWithCount(difference), false);
            } else if (difference < 0) {
                this.extractItem(slot, -difference, false);
            }
        }
    }

    private final class GuiInputItemHandler implements IItemHandlerModifiable {

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        @NotNull
        public ItemStack getStackInSlot(int slot) {
            validateSlot(slot);
            return ItemStack.EMPTY;
        }

        @Override
        @NotNull
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return storageItemHandler.insertItem(slot, stack, simulate);
        }

        @Override
        @NotNull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            validateSlot(slot);
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            validateSlot(slot);
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            validateSlot(slot);
            return storageItemHandler.canAccept(stack);
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            validateSlot(slot);
            if (!stack.isEmpty()) {
                storageItemHandler.insertItem(slot, stack, false);
            }
        }
    }

    private final class GuiOutputItemHandler implements IItemHandlerModifiable {

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        @NotNull
        public ItemStack getStackInSlot(int slot) {
            return storageItemHandler.getStackInSlot(slot);
        }

        @Override
        @NotNull
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            validateSlot(slot);
            return stack;
        }

        @Override
        @NotNull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return storageItemHandler.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            validateSlot(slot);
            return storedItem.isEmpty() ? 64 : storedItem.getMaxStackSize();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            validateSlot(slot);
            return false;
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            storageItemHandler.setStackInSlot(slot, stack);
        }
    }
}
