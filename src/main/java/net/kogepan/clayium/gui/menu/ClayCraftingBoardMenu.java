package net.kogepan.clayium.gui.menu;

import net.kogepan.clayium.blockentities.ClayCraftingBoardBlockEntity;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.client.ldlib.elements.ProgressArrow;
import net.kogepan.clayium.gui.factory.ClayCraftingBoardUIMenuType.ClayCraftingBoardUIHolder;
import net.kogepan.clayium.registries.ClayiumMenuTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.SlotItemHandler;

import com.lowdragmc.lowdraglib2.gui.holder.ModularUIContainerMenu;
import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.ScrollDisplay;
import com.lowdragmc.lowdraglib2.gui.ui.data.ScrollerMode;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ScrollerView;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ClayCraftingBoardMenu extends ModularUIContainerMenu {

    private static final int ADJACENT_COLUMNS = 6;
    private static final int ADJACENT_VISIBLE_ROWS = 10;
    private static final int ADJACENT_SLOT_SIZE = 18;
    private static final int VERTICAL_SCROLLBAR_WIDTH = 5;
    private static final int PANEL_PADDING = 6;
    private static final int PANEL_GAP = 2;

    private final Session session;

    public ClayCraftingBoardMenu(int containerId, @NotNull Inventory inventory,
                                 @NotNull ClayCraftingBoardUIHolder holder) {
        super(menuType(), containerId, inventory, holder);
        this.session = holder.getSession();
        this.session.attach();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static MenuType<ModularUIContainerMenu> menuType() {
        return (MenuType) ClayiumMenuTypes.CLAY_CRAFTING_BOARD_UI.get();
    }

    @Override
    public void slotsChanged(@NotNull Container container) {
        this.session.refreshResult();
    }

    @Override
    public void broadcastChanges() {
        this.session.refreshResult();
        super.broadcastChanges();
    }

    @Override
    @NotNull
    public ItemStack quickMoveStack(@NotNull Player player, int index) {
        if (player.level().isClientSide() || index < 0 || index >= this.slots.size()) {
            return ItemStack.EMPTY;
        }

        Slot source = this.slots.get(index);
        if (!source.mayPickup(player) || !source.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack original = source.getItem().copy();
        ItemStack sourceStack = original.copy();
        boolean moved;

        if (source == this.session.resultSlot) {
            sourceStack.getItem().onCraftedBy(sourceStack, player.level(), player);
            moved = moveInto(sourceStack, getPlayerSlots(), true);
            if (!moved) {
                return ItemStack.EMPTY;
            }
            source.onQuickCraft(sourceStack, original);
        } else if (this.session.gridSlots.contains(source)) {
            moved = moveThrough(sourceStack, this.session.adjacentSlots, false, getPlayerSlots(), true);
        } else if (this.session.adjacentSlots.contains(source)) {
            moved = moveThrough(sourceStack, this.session.gridSlots, false, getPlayerSlots(), true);
        } else if (source.container == this.inventory) {
            moved = moveThrough(sourceStack, this.session.adjacentSlots, false, this.session.gridSlots, false);
        } else {
            return ItemStack.EMPTY;
        }

        if (!moved || sourceStack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }

        source.setByPlayer(sourceStack.isEmpty() ? ItemStack.EMPTY : sourceStack, original);

        source.onTake(player, sourceStack);
        if (source == this.session.resultSlot && !sourceStack.isEmpty()) {
            player.drop(sourceStack, false);
        }
        return original;
    }

    private static boolean moveThrough(ItemStack stack, List<Slot> first, boolean firstReverse,
                                       List<Slot> second, boolean secondReverse) {
        boolean moved = moveInto(stack, first, firstReverse);
        if (!stack.isEmpty()) {
            moved |= moveInto(stack, second, secondReverse);
        }
        return moved;
    }

    private static boolean moveInto(ItemStack stack, List<Slot> destinations, boolean reverse) {
        int initialCount = stack.getCount();
        for (int pass = 0; pass < 2 && !stack.isEmpty(); pass++) {
            for (int offset = 0; offset < destinations.size() && !stack.isEmpty(); offset++) {
                int slotIndex = reverse ? destinations.size() - 1 - offset : offset;
                Slot destination = destinations.get(slotIndex);
                if ((pass == 0) != destination.hasItem()) {
                    continue;
                }
                destination.safeInsert(stack);
            }
        }
        return stack.getCount() != initialCount;
    }

    private List<Slot> getPlayerSlots() {
        return this.slots.stream().filter(slot -> slot.container == this.inventory).toList();
    }

    public List<Slot> getInputSources() {
        List<Slot> sources = new ArrayList<>(this.session.gridSlots);
        sources.addAll(this.session.adjacentSlots);
        sources.addAll(getPlayerSlots());
        return sources;
    }

    public List<Slot> getCraftingSlots() {
        return Collections.unmodifiableList(this.session.gridSlots);
    }

    public Slot getOutputSlot() {
        return this.session.resultSlot;
    }

    @Override
    public boolean canTakeItemForPickAll(@NotNull ItemStack stack, @NotNull Slot slot) {
        return slot != this.session.resultSlot && super.canTakeItemForPickAll(stack, slot);
    }

    public static final class Session {

        private final Player player;
        private final BlockPos boardPos;
        private final BlockState boardState;
        @Nullable
        private final ClayCraftingBoardBlockEntity boardEntity;
        @Nullable
        private final IItemHandler adjacentHandler;
        @Nullable
        private final BlockCapabilityCache<IItemHandler, Direction> adjacentCache;
        private final int adjacentSlotCount;
        private final BoardCraftingContainer craftSlots;
        private final ResultContainer resultSlots = new ResultContainer();
        private final List<Slot> gridSlots = new ArrayList<>();
        private final List<Slot> adjacentSlots = new ArrayList<>();
        @Nullable
        private BoardResultSlot resultSlot;
        @Nullable
        private RecipeHolder<CraftingRecipe> lastRecipe;

        public Session(@NotNull Player player, @NotNull BlockPos boardPos, @NotNull BlockState boardState,
                       @Nullable ClayCraftingBoardBlockEntity boardEntity,
                       @NotNull IItemHandlerModifiable craftingHandler, @Nullable IItemHandler adjacentHandler,
                       @Nullable BlockCapabilityCache<IItemHandler, Direction> adjacentCache,
                       int adjacentSlotCount) {
            this.player = player;
            this.boardPos = boardPos;
            this.boardState = boardState;
            this.boardEntity = boardEntity;
            this.adjacentHandler = adjacentHandler;
            this.adjacentCache = adjacentCache;
            this.adjacentSlotCount = adjacentSlotCount;
            this.craftSlots = new BoardCraftingContainer(craftingHandler, this::onCraftingChanged);
        }

        private void attach() {
            refreshResult();
        }

        public boolean isStillValid(@NotNull Player player) {
            if (!this.boardState.is(player.level().getBlockState(this.boardPos).getBlock())) {
                return false;
            }
            if (this.boardEntity != null && !Container.stillValidBlockEntity(this.boardEntity, player)) {
                return false;
            }
            if (this.adjacentCache == null) {
                return true;
            }
            IItemHandler current = this.adjacentCache.getCapability();
            return current == this.adjacentHandler && current != null && current.getSlots() == this.adjacentSlotCount;
        }

        @NotNull
        public ModularUI createUI() {
            UIElement root = new UIElement()
                    .layout(layout -> layout.paddingAll(PANEL_PADDING))
                    .addClass("panel_bg");

            if (this.adjacentHandler != null && this.adjacentSlotCount > 0) {
                int adjacentPanelWidth = adjacentPaneWidth() + PANEL_PADDING * 2;
                root.addChild(new UIElement()
                        .layout(layout -> layout
                                .positionType(TaffyPosition.ABSOLUTE)
                                .left(-adjacentPanelWidth - PANEL_GAP)
                                .top(0)
                                .width(adjacentPanelWidth)
                                .paddingAll(PANEL_PADDING))
                        .addClass("panel_bg")
                        .addChild(createAdjacentPane()));
            }
            root.addChild(createMainPane());

            return new ModularUI(UI.of(root,
                    List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC))), this.player);
        }

        private UIElement createAdjacentPane() {
            int rowWidth = ADJACENT_COLUMNS * ADJACENT_SLOT_SIZE;
            boolean needsScrolling = adjacentNeedsScrolling();
            UIElement pane = new UIElement()
                    .layout(layout -> layout.width(adjacentPaneWidth()).gapAll(3).alignItems(AlignItems.CENTER));
            pane.addChild(new CLabel()
                    .setText(Component.translatable("gui.clayium.adjacent_inventory"))
                    .layout(layout -> layout.alignSelf(AlignItems.FLEX_START)));

            UIElement rows = new UIElement()
                    .layout(layout -> layout.width(rowWidth).flexDirection(FlexDirection.COLUMN));
            int rowCount = (this.adjacentSlotCount + ADJACENT_COLUMNS - 1) / ADJACENT_COLUMNS;
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                UIElement row = new UIElement()
                        .layout(layout -> layout.width(rowWidth).height(ADJACENT_SLOT_SIZE)
                                .flexDirection(FlexDirection.ROW));
                for (int column = 0; column < ADJACENT_COLUMNS; column++) {
                    int handlerSlot = rowIndex * ADJACENT_COLUMNS + column;
                    if (handlerSlot >= this.adjacentSlotCount) {
                        break;
                    }
                    SafeItemHandlerSlot slot = new SafeItemHandlerSlot(this.adjacentHandler, handlerSlot);
                    this.adjacentSlots.add(slot);
                    row.addChild(new ItemSlot().bind(slot));
                }
                rows.addChild(row);
            }

            ScrollerView scroller = new ScrollerView();
            scroller.scrollerStyle(style -> style
                    .mode(ScrollerMode.VERTICAL)
                    .verticalScrollDisplay(needsScrolling ? ScrollDisplay.ALWAYS : ScrollDisplay.NEVER)
                    .horizontalScrollDisplay(ScrollDisplay.NEVER)
                    .minScrollPixel(ADJACENT_SLOT_SIZE)
                    .maxScrollPixel(ADJACENT_SLOT_SIZE));
            scroller.viewPort(view -> view
                    .layout(layout -> layout.paddingAll(0))
                    .style(style -> style.backgroundTexture(IGuiTexture.EMPTY)));
            scroller.layout(layout -> layout
                    .width(adjacentPaneWidth())
                    .height(ADJACENT_VISIBLE_ROWS * ADJACENT_SLOT_SIZE));
            scroller.addScrollViewChild(rows);
            pane.addChild(scroller);
            return pane;
        }

        private boolean adjacentNeedsScrolling() {
            return this.adjacentSlotCount > ADJACENT_COLUMNS * ADJACENT_VISIBLE_ROWS;
        }

        private int adjacentPaneWidth() {
            return ADJACENT_COLUMNS * ADJACENT_SLOT_SIZE + (adjacentNeedsScrolling() ? VERTICAL_SCROLLBAR_WIDTH : 0);
        }

        private UIElement createMainPane() {
            UIElement pane = new UIElement()
                    .layout(layout -> layout.width(162).gapAll(3).alignItems(AlignItems.CENTER));
            pane.addChild(new CLabel()
                    .setText(this.boardState.getBlock().getName())
                    .layout(layout -> layout.alignSelf(AlignItems.FLEX_START)));

            UIElement craftingRow = new UIElement()
                    .layout(layout -> layout.width(162).flexDirection(FlexDirection.ROW)
                            .alignItems(AlignItems.CENTER).justifyContent(AlignContent.CENTER).marginVertical(4));
            UIElement grid = new UIElement().layout(layout -> layout.flexDirection(FlexDirection.COLUMN));
            for (int rowIndex = 0; rowIndex < 3; rowIndex++) {
                UIElement row = new UIElement().layout(layout -> layout.flexDirection(FlexDirection.ROW));
                for (int column = 0; column < 3; column++) {
                    int gridIndex = rowIndex * 3 + column;
                    Slot slot = new Slot(this.craftSlots, gridIndex, 0, 0);
                    this.gridSlots.add(slot);
                    row.addChild(new ItemSlot().bind(slot));
                }
                grid.addChild(row);
            }
            craftingRow.addChild(grid);
            craftingRow.addChild(new ProgressArrow().layout(layout -> layout.width(20).marginHorizontal(8)));
            this.resultSlot = new BoardResultSlot(this.player, this.craftSlots, this.resultSlots, this);
            craftingRow.addChild(new ItemSlot().bind(this.resultSlot));
            pane.addChild(craftingRow);

            pane.addChild(new CLabel()
                    .setText(Component.translatable("gui.clayium.inventory"))
                    .layout(layout -> layout.alignSelf(AlignItems.FLEX_START)));
            pane.addChild(new InventorySlots());
            return pane;
        }

        private void onCraftingChanged() {
            if (this.boardEntity != null) {
                this.boardEntity.setChanged();
            }
            refreshResult();
        }

        private void refreshResult() {
            if (!(this.player instanceof ServerPlayer serverPlayer)) {
                return;
            }

            CraftingInput input = this.craftSlots.asCraftInput();
            Optional<RecipeHolder<CraftingRecipe>> match = serverPlayer.level().getRecipeManager()
                    .getRecipeFor(RecipeType.CRAFTING, input, serverPlayer.level(), this.lastRecipe);
            ItemStack result = ItemStack.EMPTY;
            this.lastRecipe = match.orElse(null);
            if (this.lastRecipe != null &&
                    this.resultSlots.setRecipeUsed(serverPlayer.level(), serverPlayer, this.lastRecipe)) {
                ItemStack assembled = this.lastRecipe.value().assemble(input, serverPlayer.level().registryAccess());
                if (assembled.isItemEnabled(serverPlayer.level().enabledFeatures())) {
                    result = assembled;
                }
            } else if (this.lastRecipe == null) {
                this.resultSlots.setRecipeUsed(null);
            }

            if (!ItemStack.matches(this.resultSlots.getItem(0), result)) {
                this.resultSlots.setItem(0, result);
            }
        }

        private void consumeIngredients(@NotNull Player player) {
            CraftingInput.Positioned positioned = this.craftSlots.asPositionedCraftInput();
            CraftingInput input = positioned.input();
            if (input.isEmpty()) {
                return;
            }

            net.neoforged.neoforge.common.CommonHooks.setCraftingPlayer(player);
            NonNullList<ItemStack> remaining;
            try {
                remaining = player.level().getRecipeManager()
                        .getRemainingItemsFor(RecipeType.CRAFTING, input, player.level());
            } finally {
                net.neoforged.neoforge.common.CommonHooks.setCraftingPlayer(null);
            }

            boolean[] consumedFromAdjacent = new boolean[this.craftSlots.getContainerSize()];
            for (int row = 0; row < input.height(); row++) {
                for (int column = 0; column < input.width(); column++) {
                    int gridIndex = column + positioned.left() + (row + positioned.top()) * this.craftSlots.getWidth();
                    ItemStack template = this.craftSlots.getItem(gridIndex).copy();
                    if (template.isEmpty()) {
                        continue;
                    }

                    consumedFromAdjacent[gridIndex] = consumeAdjacent(template);
                    if (!consumedFromAdjacent[gridIndex]) {
                        this.craftSlots.removeItem(gridIndex, 1);
                    }
                }
            }

            for (int row = 0; row < input.height(); row++) {
                for (int column = 0; column < input.width(); column++) {
                    int gridIndex = column + positioned.left() + (row + positioned.top()) * this.craftSlots.getWidth();
                    ItemStack remainder = remaining.get(column + row * input.width());
                    if (!remainder.isEmpty()) {
                        if (consumedFromAdjacent[gridIndex]) {
                            remainder = ItemHandlerHelper.insertItemStacked(this.adjacentHandler, remainder, false);
                            giveOrDrop(player, remainder);
                        } else {
                            placeGridRemainder(player, gridIndex, remainder);
                        }
                    }
                }
            }
            refreshResult();
        }

        private boolean consumeAdjacent(ItemStack template) {
            if (this.adjacentHandler == null) {
                return false;
            }
            for (int slot = 0; slot < this.adjacentSlotCount; slot++) {
                ItemStack present = this.adjacentHandler.getStackInSlot(slot);
                if (!present.isEmpty() && ItemStack.isSameItemSameComponents(present, template)) {
                    ItemStack simulated = this.adjacentHandler.extractItem(slot, 1, true);
                    if (!simulated.isEmpty() && ItemStack.isSameItemSameComponents(simulated, template)) {
                        ItemStack extracted = this.adjacentHandler.extractItem(slot, 1, false);
                        if (!extracted.isEmpty() && ItemStack.isSameItemSameComponents(extracted, template)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private void placeGridRemainder(Player player, int gridIndex, ItemStack remainder) {
            ItemStack current = this.craftSlots.getItem(gridIndex);
            if (current.isEmpty()) {
                this.craftSlots.setItem(gridIndex, remainder);
            } else if (ItemStack.isSameItemSameComponents(current, remainder) &&
                    current.getCount() + remainder.getCount() <= current.getMaxStackSize()) {
                        ItemStack combined = current.copy();
                        combined.grow(remainder.getCount());
                        this.craftSlots.setItem(gridIndex, combined);
                    } else {
                        giveOrDrop(player, remainder);
                    }
        }

        private static void giveOrDrop(Player player, ItemStack stack) {
            if (!stack.isEmpty() && !player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }
    }

    private static final class BoardResultSlot extends ResultSlot {

        private final Session session;

        private BoardResultSlot(Player player, CraftingContainer craftSlots, Container resultSlots, Session session) {
            super(player, craftSlots, resultSlots, 0, 0, 0);
            this.session = session;
        }

        @Override
        public boolean mayPickup(@NotNull Player player) {
            this.session.refreshResult();
            return this.hasItem();
        }

        @Override
        public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
            this.checkTakeAchievements(stack);
            this.session.consumeIngredients(player);
        }
    }

    private static final class BoardCraftingContainer implements CraftingContainer {

        private final IItemHandlerModifiable handler;
        private final Runnable changed;

        private BoardCraftingContainer(IItemHandlerModifiable handler, Runnable changed) {
            this.handler = handler;
            this.changed = changed;
        }

        @Override
        public int getContainerSize() {
            return this.handler.getSlots();
        }

        @Override
        public boolean isEmpty() {
            for (int slot = 0; slot < this.handler.getSlots(); slot++) {
                if (!this.handler.getStackInSlot(slot).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return slot >= 0 && slot < this.handler.getSlots() ? this.handler.getStackInSlot(slot) : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack extracted = this.handler.extractItem(slot, amount, false);
            if (!extracted.isEmpty()) {
                this.changed.run();
            }
            return extracted;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return this.handler.extractItem(slot, this.handler.getSlotLimit(slot), false);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            this.handler.setStackInSlot(slot, stack);
            this.changed.run();
        }

        @Override
        public void setChanged() {
            this.changed.run();
        }

        @Override
        public boolean stillValid(@NotNull Player player) {
            return true;
        }

        @Override
        public void clearContent() {
            for (int slot = 0; slot < this.handler.getSlots(); slot++) {
                this.handler.setStackInSlot(slot, ItemStack.EMPTY);
            }
            this.changed.run();
        }

        @Override
        public int getWidth() {
            return 3;
        }

        @Override
        public int getHeight() {
            return 3;
        }

        @Override
        public List<ItemStack> getItems() {
            List<ItemStack> items = new ArrayList<>(this.handler.getSlots());
            for (int slot = 0; slot < this.handler.getSlots(); slot++) {
                items.add(this.handler.getStackInSlot(slot));
            }
            return List.copyOf(items);
        }

        @Override
        public void fillStackedContents(@NotNull StackedContents contents) {
            for (int slot = 0; slot < this.handler.getSlots(); slot++) {
                contents.accountSimpleStack(this.handler.getStackInSlot(slot));
            }
        }
    }

    private static final class SafeItemHandlerSlot extends SlotItemHandler {

        private SafeItemHandlerSlot(IItemHandler handler, int index) {
            super(handler, index, 0, 0);
        }

        @Override
        public void set(@NotNull ItemStack stack) {
            if (getItemHandler() instanceof IItemHandlerModifiable modifiable) {
                modifiable.setStackInSlot(this.index, stack);
                setChanged();
                return;
            }
            applyTransition(getItem().copy(), stack);
        }

        @Override
        public void setByPlayer(@NotNull ItemStack newStack, @NotNull ItemStack oldStack) {
            if (getItemHandler() instanceof IItemHandlerModifiable modifiable) {
                modifiable.setStackInSlot(this.index, newStack);
                setChanged();
                return;
            }
            applyTransition(oldStack, newStack);
        }

        private void applyTransition(ItemStack oldStack, ItemStack newStack) {
            if (ItemStack.isSameItemSameComponents(oldStack, newStack)) {
                int difference = newStack.getCount() - oldStack.getCount();
                if (difference > 0) {
                    getItemHandler().insertItem(this.index, newStack.copyWithCount(difference), false);
                } else if (difference < 0) {
                    getItemHandler().extractItem(this.index, -difference, false);
                }
                return;
            }

            ItemStack removed = getItemHandler().extractItem(this.index, oldStack.getCount(), false);
            ItemStack remainder = getItemHandler().insertItem(this.index, newStack.copy(), false);
            if (!remainder.isEmpty()) {
                int inserted = newStack.getCount() - remainder.getCount();
                if (inserted > 0) {
                    getItemHandler().extractItem(this.index, inserted, false);
                }
                ItemHandlerHelper.insertItemStacked(getItemHandler(), removed, false);
            }
        }

        @Override
        @NotNull
        public ItemStack safeInsert(@NotNull ItemStack stack, int increment) {
            if (stack.isEmpty() || !mayPlace(stack)) {
                return stack;
            }
            int amount = Math.min(increment, stack.getCount());
            ItemStack toInsert = stack.copyWithCount(amount);
            ItemStack remainder = getItemHandler().insertItem(this.index, toInsert, false);
            stack.shrink(amount - remainder.getCount());
            return stack;
        }

        @Override
        public boolean allowModification(@NotNull Player player) {
            return mayPickup(player);
        }
    }
}
