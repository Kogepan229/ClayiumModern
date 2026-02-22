package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.trait.AutoIOTrait;
import net.kogepan.clayium.client.ldlib.elements.PhantomItemSlot;
import net.kogepan.clayium.client.ldlib.textures.SlotTextures;
import net.kogepan.clayium.inventory.ClayiumItemStackHandler;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.slot.ItemHandlerSlot;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static net.kogepan.clayium.registries.ClayiumBlockEntityTypes.AUTO_CLAY_CONDENSER_BLOCK_ENTITY;

public class AutoClayCondenserBlockEntity extends ClayContainerBlockEntity {

    private static final int ROWS = 4;
    private static final int COLS = 5;
    private static final int MAIN_SLOTS = ROWS * COLS;
    private static final int CONFIG_SLOT = 0;

    private static final int DEFAULT_MAX_TIER_WHEN_EMPTY = 13;

    private final ClayiumItemStackHandler mainInventory;
    private final ClayiumItemStackHandler configInventory;
    private final AutoClayCondenserItemHandler exposedHandler;

    /** Tier of the 9 items currently being compressed (-1 if none). */
    private int workInProgressTier = -1;
    /** Ticks remaining until current craft completes. */
    private int craftTicksRemaining = 0;

    public AutoClayCondenserBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(AUTO_CLAY_CONDENSER_BLOCK_ENTITY.get(), pos, blockState,
                Arrays.asList(MachineIOMode.NONE, MachineIOMode.ALL),
                Arrays.asList(MachineIOMode.NONE, MachineIOMode.ALL));

        this.mainInventory = new ClayiumItemStackHandler(this, MAIN_SLOTS) {

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return getClayTier(stack) >= 0;
            }
        };
        this.configInventory = new ClayiumItemStackHandler(this, 1) {

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return getClayTier(stack) >= 0;
            }
        };
        this.exposedHandler = new AutoClayCondenserItemHandler();

        this.addTrait(new AutoIOTrait.Combined(this, this.tier, false));
    }

    @Override
    public void initDefaultRoutes() {
        this.inputModes.setMode(Direction.UP, MachineIOMode.ALL);
        this.outputModes.setMode(Direction.DOWN, MachineIOMode.ALL);
    }

    @Override
    public IItemHandlerModifiable getInputInventory() {
        return this.mainInventory;
    }

    @Override
    public IItemHandlerModifiable getOutputInventory() {
        return this.exposedHandler;
    }

    @Override
    protected void tick() {
        super.tick();

        Level level = this.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        // Time-based compression; Mk II can process multiple crafts per tick.
        int maxIterations = this.tier >= 7 ? 256 : 2;
        for (int iter = 0; iter < maxIterations; iter++) {
            if (this.workInProgressTier >= 0) {
                this.craftTicksRemaining--;
                if (this.craftTicksRemaining <= 0) {
                    produceClay(this.workInProgressTier + 1, 1);
                    sortInventory();
                    this.workInProgressTier = -1;
                }
            }
            if (this.workInProgressTier >= 0) {
                continue;
            }
            if (!tryStartCraft()) {
                break;
            }
        }
    }

    /** Tries to consume 9 items and start one craft. Returns true if started. */
    private boolean tryStartCraft() {
        int maxTier = getConfiguredMaxTier();
        int[] quantities = getQuantityOfClay();
        int consumedTier = getConsumableTier(quantities, maxTier);
        if (consumedTier < 0) {
            return false;
        }
        if (!canProduceClay(consumedTier + 1, 1)) {
            return false;
        }
        consumeClay(consumedTier, 9);
        this.workInProgressTier = consumedTier;
        this.craftTicksRemaining = getCraftTicks();
        return true;
    }

    /** Ticks per compression. Mk II faster. */
    private int getCraftTicks() {
        return this.tier >= 7 ? 1 : 4;
    }

    /** Empty filter slot = max tier 13; otherwise use tier of item in slot. */
    private int getConfiguredMaxTier() {
        ItemStack stack = this.configInventory.getStackInSlot(CONFIG_SLOT);
        int tier = getClayTier(stack);
        if (tier >= 0) {
            return tier;
        }
        return DEFAULT_MAX_TIER_WHEN_EMPTY;
    }

    private int[] getQuantityOfClay() {
        int maxTierCount = ClayiumBlocks.COMPRESSED_CLAYS.size() + 1;
        int[] num = new int[maxTierCount];

        for (int i = 0; i < MAIN_SLOTS; i++) {
            ItemStack stack = this.mainInventory.getStackInSlot(i);
            int tier = getClayTier(stack);
            if (tier >= 0 && tier < num.length) {
                num[tier] += stack.getCount();
            }
        }

        return num;
    }

    private int getConsumableTier(int[] quantities, int maxTier) {
        int upper = quantities.length;
        if (maxTier >= 0 && maxTier < upper) {
            upper = maxTier;
        }

        for (int tier = upper - 1; tier >= 0; tier--) {
            if (quantities[tier] >= 9) {
                return tier;
            }
        }
        return -1;
    }

    private void consumeClay(int tier, int amount) {
        int remaining = amount;
        for (int i = 0; i < MAIN_SLOTS && remaining > 0; i++) {
            ItemStack stack = this.mainInventory.getStackInSlot(i);
            if (getClayTier(stack) != tier) continue;
            int toConsume = Math.min(remaining, stack.getCount());
            ItemStack copy = stack.copy();
            copy.shrink(toConsume);
            this.mainInventory.setStackInSlot(i, copy.isEmpty() ? ItemStack.EMPTY : copy);
            remaining -= toConsume;
        }
    }

    private boolean canProduceClay(int tier, int amount) {
        int remaining = amount;

        // First, try merge into existing stacks of the same tier.
        for (int i = 0; i < MAIN_SLOTS && remaining > 0; i++) {
            ItemStack stack = this.mainInventory.getStackInSlot(i);
            if (getClayTier(stack) != tier) continue;
            int space = Math.min(stack.getMaxStackSize(), this.mainInventory.getSlotLimit(i)) - stack.getCount();
            if (space > 0) {
                int toAdd = Math.min(space, remaining);
                remaining -= toAdd;
            }
        }

        // Then, look for empty slots.
        for (int i = 0; i < MAIN_SLOTS && remaining > 0; i++) {
            ItemStack stack = this.mainInventory.getStackInSlot(i);
            if (stack.isEmpty()) {
                remaining--;
            }
        }

        return remaining <= 0;
    }

    private void produceClay(int tier, int amount) {
        int remaining = amount;

        for (int i = 0; i < MAIN_SLOTS && remaining > 0; i++) {
            ItemStack stack = this.mainInventory.getStackInSlot(i);
            if (getClayTier(stack) != tier) continue;

            int space = Math.min(stack.getMaxStackSize(), this.mainInventory.getSlotLimit(i)) - stack.getCount();
            if (space <= 0) continue;

            int toAdd = Math.min(space, remaining);
            ItemStack copy = stack.copy();
            copy.grow(toAdd);
            this.mainInventory.setStackInSlot(i, copy);
            remaining -= toAdd;
        }

        for (int i = 0; i < MAIN_SLOTS && remaining > 0; i++) {
            ItemStack stack = this.mainInventory.getStackInSlot(i);
            if (!stack.isEmpty()) continue;

            this.mainInventory.setStackInSlot(i, createClayStack(tier, 1));
            remaining--;
        }
    }

    private void sortInventory() {
        int[] quantities = getQuantityOfClay();

        for (int i = 0; i < MAIN_SLOTS; i++) {
            this.mainInventory.setStackInSlot(i, ItemStack.EMPTY);
        }

        int slotIndex = 0;
        for (int tier = 0; tier < quantities.length && slotIndex < MAIN_SLOTS; tier++) {
            int remaining = quantities[tier];
            while (remaining > 0 && slotIndex < MAIN_SLOTS) {
                int maxStack = Math.min(64, this.mainInventory.getSlotLimit(slotIndex));
                int toPlace = Math.min(maxStack, remaining);
                this.mainInventory.setStackInSlot(slotIndex, createClayStack(tier, toPlace));
                remaining -= toPlace;
                slotIndex++;
            }
        }
    }

    private static int getClayTier(ItemStack stack) {
        if (stack.isEmpty()) {
            return -1;
        }

        Block block = Block.byItem(stack.getItem());
        if (block == Blocks.CLAY) {
            return 0;
        }

        for (var entry : ClayiumBlocks.COMPRESSED_CLAYS.int2ObjectEntrySet()) {
            if (entry.getValue().get().asItem() == stack.getItem()) {
                return entry.getIntKey() + 1;
            }
        }

        return -1;
    }

    private static ItemStack createClayStack(int tier, int amount) {
        if (tier == 0) {
            return new ItemStack(Blocks.CLAY, amount);
        }
        Block block = ClayiumBlocks.COMPRESSED_CLAYS.get(tier - 1).get();
        return new ItemStack(block.asItem(), amount);
    }

    private boolean canExport(ItemStack stack) {
        int tier = getClayTier(stack);
        if (tier < 0) return false;
        int maxTier = getConfiguredMaxTier();
        return tier >= maxTier;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("mainInventory", this.mainInventory.serializeNBT(provider));
        tag.put("configInventory", this.configInventory.serializeNBT(provider));
        tag.putInt("workInProgressTier", this.workInProgressTier);
        tag.putInt("craftTicksRemaining", this.craftTicksRemaining);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("mainInventory")) {
            this.mainInventory.deserializeNBT(provider, tag.getCompound("mainInventory"));
        }
        if (tag.contains("configInventory")) {
            this.configInventory.deserializeNBT(provider, tag.getCompound("configInventory"));
        }
        this.workInProgressTier = tag.getInt("workInProgressTier");
        this.craftTicksRemaining = tag.getInt("craftTicksRemaining");
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        UIElement rowContainer = new UIElement().layout(
                layout -> layout.flexDirection(FlexDirection.ROW)
                        .alignItems(AlignItems.FLEX_START)
                        .flexGrow(1));
        root.addChild(rowContainer);

        // Left spacer: same width as right block so the grid stays in the center.
        UIElement leftSpacer = new UIElement().layout(layout -> layout.flexGrow(1).flexShrink(1).flexBasis(0));
        rowContainer.addChild(leftSpacer);

        UIElement grid = new UIElement().layout(
                layout -> layout.justifyContent(AlignContent.CENTER).flexGrow(0).flexShrink(1).flexBasisAuto());
        rowContainer.addChild(grid);

        for (int row = 0; row < ROWS; row++) {
            UIElement rowElement = new UIElement().layout(
                    layout -> layout.flexDirection(FlexDirection.ROW).justifyContent(AlignContent.CENTER));
            grid.addChild(rowElement);
            for (int col = 0; col < COLS; col++) {
                int index = col + row * COLS;
                rowElement.addChild(new ItemSlot()
                        .bind(new ItemHandlerSlot(this.mainInventory, index)));
            }
        }

        // Right block: same flex width as left spacer; config slot at its right edge.
        UIElement rightBlock = new UIElement().layout(
                layout -> layout.flexDirection(FlexDirection.ROW)
                        .alignItems(AlignItems.FLEX_START)
                        .flexGrow(1).flexShrink(1).flexBasis(0));
        rowContainer.addChild(rightBlock);
        UIElement configSlotWrapper = new UIElement().layout(
                layout -> layout.marginLeft(8));
        configSlotWrapper.addChild(PhantomItemSlot.create(this.configInventory, CONFIG_SLOT).slotStyle(
                style -> style.slotOverlay(SlotTextures.PHANTOM_ITEM_SLOT_OVERLAY).showSlotOverlayOnlyEmpty(true)));
        rightBlock.addChild(configSlotWrapper);
    }

    private class AutoClayCondenserItemHandler implements IItemHandlerModifiable {

        @Override
        public int getSlots() {
            return MAIN_SLOTS;
        }

        @Override
        @NotNull
        public ItemStack getStackInSlot(int slot) {
            if (slot < 0 || slot >= MAIN_SLOTS) return ItemStack.EMPTY;
            return mainInventory.getStackInSlot(slot);
        }

        @Override
        @NotNull
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.isEmpty() || getClayTier(stack) < 0) {
                return stack;
            }

            ItemStack remaining = stack.copy();
            // Insert only into the first 3 rows (0..14), like the original implementation.
            for (int i = 0; i < MAIN_SLOTS - COLS && !remaining.isEmpty(); i++) {
                remaining = mainInventory.insertItem(i, remaining, simulate);
            }
            return remaining;
        }

        @Override
        @NotNull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            // Export from first exportable slot only (callers may call again for more).
            for (int i = 0; i < MAIN_SLOTS; i++) {
                ItemStack stack = mainInventory.getStackInSlot(i);
                if (stack.isEmpty() || !canExport(stack)) continue;
                return mainInventory.extractItem(i, Math.min(amount, stack.getCount()), simulate);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return mainInventory.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            // Only clay and compressed clay blocks are accepted.
            return getClayTier(stack) >= 0;
        }

        @Override
        public void setStackInSlot(int i, @NotNull ItemStack itemStack) {}
    }
}
