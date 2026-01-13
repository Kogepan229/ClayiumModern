package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.client.ldlib.elements.ClayWorkTableButton;
import net.kogepan.clayium.client.ldlib.elements.LargeItemSlot;
import net.kogepan.clayium.client.ldlib.elements.ProgressArrow;
import net.kogepan.clayium.client.ldlib.textures.ClayWorkTableButtonTextures;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.recipes.ClayWorkTableRecipe;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.utils.DroppableItemStackHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import lombok.Getter;
import org.appliedenergistics.yoga.YogaAlign;
import org.appliedenergistics.yoga.YogaFlexDirection;
import org.appliedenergistics.yoga.YogaJustify;

import java.util.ArrayList;
import java.util.List;

public class ClayWorkTableBlockEntity extends BlockEntity implements ISyncPersistRPCBlockEntity {

    private static final int INGREDIENT_SLOT = 0;
    private static final int RESULT_SLOT = 1;
    private static final int BYPRODUCT_SLOT = 2;
    private static final int TOOL_SLOT = 3;

    @Getter
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Getter
    @Persisted(subPersisted = true)
    private final DroppableItemStackHandler inventory = new DroppableItemStackHandler(4) {

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
                validButtons.add(this.processingRecipeHolder.value().button());
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
                .filter(r -> r.ingredient().test(ingredient) &&
                        ingredient.getCount() >= r.cost() &&
                        this.inventory.insertItem(RESULT_SLOT, r.result(), true).isEmpty() &&
                        this.inventory.insertItem(BYPRODUCT_SLOT, r.byproduct(), true).isEmpty())
                .map(ClayWorkTableRecipe::button).distinct().filter(this::checkTool).toList());
    }

    private boolean checkTool(int button) {
        if (button < 2) {
            return true;
        }

        ItemStack tool = this.inventory.getStackInSlot(TOOL_SLOT);
        if (tool.isEmpty() || !tool.isDamageableItem() || tool.getMaxDamage() - tool.getDamageValue() < 1) {
            return false;
        }

        return switch (button) {
            case 2 -> tool.is(ClayiumItems.CLAY_ROLLING_PIN);
            case 3, 5 -> tool.is(ClayiumItems.CLAY_SPATULA) || tool.is(ClayiumItems.CLAY_SLICER);
            case 4 -> tool.is(ClayiumItems.CLAY_SPATULA);
            default -> false;
        };
    }

    private void hurtTool(int button) {
        if (button >= 2) {
            ItemStack tool = this.inventory.getStackInSlot(TOOL_SLOT);
            tool.hurtAndBreak(1, getServerLevel(), null, item -> {});
        }
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
                        return r.ingredient().test(ingredient) && ingredient.getCount() >= r.cost() &&
                                r.button() == button;
                    })
                    .findFirst();

            if (foundRecipeHolder.isEmpty()) return;

            this.processingRecipeHolder = foundRecipeHolder.get();
            final var recipe = this.processingRecipeHolder.value();
            this.progress++;
            this.hurtTool(recipe.button());
            this.inventory.extractItem(INGREDIENT_SLOT, recipe.ingredient().getAmount(), false);
            this.validButtons.add(recipe.button());
        } else {
            final var recipe = this.processingRecipeHolder.value();
            this.progress++;
            this.hurtTool(recipe.button());

            if (this.progress == recipe.cost()) {
                this.inventory.insertItem(1, recipe.result().copy(), false);
                validButtons.clear();
                this.processingRecipeHolder = null;
                this.progress = 0;
                checkValidRecipe();
            }
        }
    }

    private ClayWorkTableButton createWorkTableButton(ClayWorkTableButtonTextures.ButtonTexture texture, int index) {
        ClayWorkTableButton button = new ClayWorkTableButton(texture);
        button.bind(DataBindingBuilder.boolS2C(() -> validButtons.contains(index)).build());
        button.addServerEventListener(UIEvents.CLICK, e -> this.onClickButton(index));
        return button;
    }

    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
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
                                        (float) progress / this.processingRecipeHolder.value().cost() : 0).build())
                                .layout(layout -> layout.width(80)))
                        .addChild(new UIElement()
                                .layout(layout -> layout.flexDirection(YogaFlexDirection.ROW)
                                        .setJustifyContent(YogaJustify.CENTER).marginTop(5))
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
}
