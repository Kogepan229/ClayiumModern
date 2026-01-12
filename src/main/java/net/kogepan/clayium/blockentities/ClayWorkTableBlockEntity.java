package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.client.ldlib.elements.LargeItemSlot;
import net.kogepan.clayium.client.ldlib.elements.ProgressArrow;
import net.kogepan.clayium.client.ldlib.textures.ClayWorkTableButtonTextures;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.recipes.ClayWorkTableRecipe;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.IBindable;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.IDataConsumer;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.IDataProvider;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEventListener;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import com.lowdragmc.lowdraglib2.gui.util.ITickable;
import com.lowdragmc.lowdraglib2.syncdata.ISubscription;
import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import lombok.Getter;
import org.appliedenergistics.yoga.YogaAlign;
import org.appliedenergistics.yoga.YogaFlexDirection;
import org.appliedenergistics.yoga.YogaJustify;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClayWorkTableBlockEntity extends BlockEntity implements ISyncPersistRPCBlockEntity {

    private static final int INGREDIENT_SLOT = 0;
    private static final int RESULT_SLOT = 1;
    private static final int BYPRODUCT_SLOT = 2;

    @Getter
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Persisted(subPersisted = true)
    private final ItemStackHandler inventory = new ItemStackHandler(4) {

        @Override
        protected void onContentsChanged(int slot) {
            if (level == null || level.isClientSide()) return;

            checkValidRecipe();

            setChanged();
        }
    };

    @DescSynced
    @Persisted
    private int progress = 0;

    @Persisted
    private RecipeHolder<ClayWorkTableRecipe> processingRecipeHolder = null;

    private final List<Integer> validButtons = new ArrayList<>();

    public ClayWorkTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(ClayiumBlockEntityTypes.CLAY_WORK_TABLE_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (level != null && !level.isClientSide()) {
            if (this.processingRecipeHolder != null) {
                validButtons.add(this.processingRecipeHolder.value().getButton());
            } else {
                this.checkValidRecipe();
            }
        }
    }

    private void checkValidRecipe() {
        if (level == null) return;

        if (this.processingRecipeHolder != null) return;

        validButtons.clear();

        ItemStack ingredient = this.inventory.getStackInSlot(INGREDIENT_SLOT);
        if (ingredient.isEmpty()) return;

        var recipes = level.getRecipeManager()
                .getAllRecipesFor(ClayiumRecipeTypes.CLAY_WORK_TABLE_RECIPE_TYPE.get());

        validButtons.addAll(recipes.stream().map(RecipeHolder::value)
                .filter(r -> r.getIngredient().test(ingredient) &&
                        ingredient.getCount() >= r.getCost() &&
                        this.inventory.insertItem(RESULT_SLOT, r.getResult(), true).isEmpty() &&
                        this.inventory.insertItem(BYPRODUCT_SLOT, r.getByproduct(), true).isEmpty())
                .map(ClayWorkTableRecipe::getButton).distinct().toList());
    }

    private void onClickButton(int button) {
        if (level == null || !this.validButtons.contains(button)) return;

        if (this.processingRecipeHolder == null) {
            this.validButtons.clear();

            ItemStack ingredient = this.inventory.getStackInSlot(INGREDIENT_SLOT);
            if (ingredient.isEmpty()) return;

            var recipes = level.getRecipeManager()
                    .getAllRecipesFor(ClayiumRecipeTypes.CLAY_WORK_TABLE_RECIPE_TYPE.get());

            var foundRecipeHolder = recipes.stream()
                    .filter(holder -> {
                        var r = holder.value();
                        return r.getIngredient().test(ingredient) && ingredient.getCount() >= r.getCost() &&
                                r.getButton() == button;
                    })
                    .findFirst();

            if (foundRecipeHolder.isEmpty()) return;

            this.processingRecipeHolder = foundRecipeHolder.get();
            final var recipe = this.processingRecipeHolder.value();
            this.validButtons.add(recipe.getButton());
            this.inventory.extractItem(INGREDIENT_SLOT, recipe.getIngredient().getAmount(), false);
            this.progress++;
        } else {
            this.progress++;

            final var recipe = this.processingRecipeHolder.value();
            if (this.progress == recipe.getCost()) {
                this.inventory.insertItem(1, recipe.getResult().copy(), false);
                validButtons.clear();
                this.processingRecipeHolder = null;
                this.progress = 0;
                checkValidRecipe();
            }
        }
    }

    private WorkTableButton createWorkTableButton(ClayWorkTableButtonTextures.ButtonTexture texture, int index) {
        WorkTableButton button = new WorkTableButton(texture);
        button.bind(DataBindingBuilder.boolS2C(() -> validButtons.contains(index)).build());
        button.addServerEventListener(UIEvents.CLICK, e -> this.onClickButton(index));
        return button;
    }

    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        Clayium.LOGGER.info("Creating UI");
        var root = new UIElement().layout(layout -> layout
                .paddingAll(6)
                .setJustifyContent(YogaJustify.CENTER))
                .addClass("panel_bg");
        root.addChild(
                new CLabel().setText(this.getBlockState().getBlock().getName().getString()));
        root.addChild(new UIElement().layout(layout -> layout.flexDirection(YogaFlexDirection.ROW).paddingHorizontal(4))
                .addChild(new LargeItemSlot().itemSlot(slot -> slot.bind(inventory, INGREDIENT_SLOT))
                        .layout(layout -> layout.marginTop(15)))
                .addChild(new UIElement()
                        .layout(layout -> layout.flexGrow(1).paddingHorizontal(0).setAlignItems(YogaAlign.CENTER))
                        .addChild(new UIElement()
                                .layout(layout -> layout.flexDirection(YogaFlexDirection.ROW)
                                        .setJustifyContent(YogaJustify.CENTER))
                                .addChild(new ItemSlot().bind(inventory, 3)))
                        .addChild(new ProgressArrow().bind(
                                DataBindingBuilder.floatValS2C(() -> this.processingRecipeHolder != null ?
                                        (float) progress / this.processingRecipeHolder.value().getCost() : 0).build())
                                .layout(layout -> layout.width(80)))
                        .addChild(new UIElement()
                                .layout(layout -> layout.flexDirection(YogaFlexDirection.ROW)
                                        .setJustifyContent(YogaJustify.CENTER).marginTop(4))
                                .addChild(createWorkTableButton(ClayWorkTableButtonTextures.BUTTON1, 0))
                                .addChild(createWorkTableButton(ClayWorkTableButtonTextures.BUTTON2, 1))
                                .addChild(createWorkTableButton(ClayWorkTableButtonTextures.BUTTON3, 2))
                                .addChild(createWorkTableButton(ClayWorkTableButtonTextures.BUTTON4, 3))
                                .addChild(createWorkTableButton(ClayWorkTableButtonTextures.BUTTON5, 4))
                                .addChild(createWorkTableButton(ClayWorkTableButtonTextures.BUTTON6, 5))))
                .addChild(new UIElement()
                        .layout(layout -> layout.marginTop(15).gapAll(3).setAlignItems(YogaAlign.CENTER))
                        .addChild(new LargeItemSlot().itemSlot(slot -> slot.bind(inventory, RESULT_SLOT)))
                        .addChild(new ItemSlot().bind(inventory, BYPRODUCT_SLOT))));

        root.addChild(new CLabel().setText("Inventory"));
        root.addChild(new InventorySlots());
        return new ModularUI(UI.of(root, List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC))),
                holder.player);
    }

    private static class WorkTableButton extends Button implements IBindable<Boolean>, IDataConsumer<Boolean> {

        private final ClayWorkTableButtonTextures.ButtonTexture texture;

        private final Map<IDataProvider<Boolean>, ISubscription> dataSources = new LinkedHashMap<>();

        WorkTableButton(ClayWorkTableButtonTextures.ButtonTexture texture) {
            super();
            noText();
            layout(l -> l.width(16).height(16));
            buttonStyle(style -> style
                    .baseTexture(texture.base)
                    .hoverTexture(texture.hovered)
                    .pressedTexture(texture.hovered));

            this.texture = texture;
        }

        @Override
        public void drawBackgroundAdditional(@NotNull GUIContext guiContext) {
            if (this.isActive()) {
                super.drawBackgroundAdditional(guiContext);
            } else {
                guiContext.drawTexture(texture.disabled, getPositionX(), getPositionY(), getSizeWidth(),
                        getSizeHeight());
            }
        }

        @Override
        public UIElement bindDataSource(@NotNull IDataProvider<Boolean> dataProvider) {
            UIEventListener tickableListener;
            if (dataProvider instanceof ITickable tickable) {
                tickableListener = e -> tickable.tick();
                addEventListener(UIEvents.TICK, tickableListener);
            } else {
                tickableListener = null;
            }
            var subscription = dataProvider.registerListener(this::setActive, true);
            if (tickableListener != null) {
                subscription.andThen(() -> removeEventListener(UIEvents.TICK, tickableListener));
            }
            this.dataSources.put(dataProvider, subscription);
            return this;
        }

        @Override
        public UIElement unbindDataSource(@NotNull IDataProvider<Boolean> dataProvider) {
            var removed = this.dataSources.remove(dataProvider);
            if (removed != null) {
                removed.unsubscribe();
            }
            return this;
        }

        @Override
        public Boolean getValue() {
            return this.isActive();
        }

        @Override
        public WorkTableButton setValue(@Nullable Boolean value) {
            this.setActive(value != null && value);
            return this;
        }
    }
}
