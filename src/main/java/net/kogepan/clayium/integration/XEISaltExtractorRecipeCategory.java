package net.kogepan.clayium.integration;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.client.ldlib.elements.ItemSlotXEI;
import net.kogepan.clayium.client.ldlib.elements.ProgressArrow;
import net.kogepan.clayium.client.ldlib.textures.XEITextures;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredBlock;

import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import com.lowdragmc.lowdraglib2.integration.xei.IngredientIO;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;

import java.util.List;

public class XEISaltExtractorRecipeCategory {

    private static final ResourceLocation ID = Clayium.id("salt_extractor");
    private static final List<XEISaltExtractorRecipe> RECIPES = List.of(
            new XEISaltExtractorRecipe(ClayiumBlocks.SALT_EXTRACTOR_BLOCKS.get(4), 4, 0.5, CEUtils.milliCeToLong(15)),
            new XEISaltExtractorRecipe(ClayiumBlocks.SALT_EXTRACTOR_BLOCKS.get(5), 5, 2, CEUtils.milliCeToLong(60)),
            new XEISaltExtractorRecipe(ClayiumBlocks.SALT_EXTRACTOR_BLOCKS.get(6), 6, 10, CEUtils.milliCeToLong(300)),
            new XEISaltExtractorRecipe(ClayiumBlocks.SALT_EXTRACTOR_BLOCKS.get(7), 7, 80, CEUtils.ceToLong(2.4)));

    public static ResourceLocation getId() {
        return ID;
    }

    public static DeferredBlock<ClayContainerBlock> getIcon() {
        return ClayiumBlocks.SALT_EXTRACTOR_BLOCKS.get(4);
    }

    public static List<XEISaltExtractorRecipe> getRecipes() {
        return RECIPES;
    }

    public record XEISaltExtractorRecipe(DeferredBlock<ClayContainerBlock> machine, int tier, double itemPerTick,
                                         long cePerTick) {

        public static final int WIDTH = 174;
        public static final int HEIGHT = 70;
        private static final int PROGRESS_ARROW_WIDTH = 20;

        public ModularUI createModularUI() {
            var root = new UIElement().layout(layout -> layout
                    .width(WIDTH)
                    .height(HEIGHT)
                    .paddingAll(6)
                    .justifyContent(AlignContent.CENTER));

            root.addChild(new UIElement().layout(layout -> layout.width(28).height(11))
                    .style(style -> style.backgroundTexture(XEITextures.BADGE)));

            UIElement inputContainer = new UIElement()
                    .layout(layout -> layout.flexDirection(FlexDirection.ROW).width(69).height(18)
                            .justifyContent(AlignContent.FLEX_END))
                    .style(style -> style.background(XEITextures.SLOT));
            UIElement outputContainer = new UIElement()
                    .layout(layout -> layout.flexDirection(FlexDirection.ROW).width(69).height(18))
                    .style(style -> style.background(XEITextures.SLOT));

            outputContainer.addChild(new ItemSlotXEI().xeiRecipeIngredient(IngredientIO.OUTPUT,
                    Ingredient.of(ClayiumItems.SALT_DUST), 1).style(style -> style.background(IGuiTexture.EMPTY)));

            root.addChild(new UIElement()
                    .layout(layout -> layout.marginTop(3).gapAll(3).flexDirection(FlexDirection.ROW))
                    .addChild(inputContainer)
                    .addChild(new UIElement()
                            .layout(layout -> layout.width(PROGRESS_ARROW_WIDTH).height(20)
                                    .alignItems(AlignItems.CENTER))
                            .addChild(new ItemSlotXEI().xeiRecipeIngredient(IngredientIO.CATALYST,
                                    Ingredient.of(this.machine.get()), 1)
                                    .style(style -> style.background(IGuiTexture.EMPTY))
                                    .layout(layout -> layout.width(18).height(18).marginTop(-18)))
                            .addChild(new ProgressArrow().bindDataSource(SupplierDataSource
                                    .of(() -> {
                                        ClientLevel level = Minecraft.getInstance().level;
                                        final long clientTick = level != null ? level.getGameTime() : 0;
                                        final int threshold = PROGRESS_ARROW_WIDTH * 2;
                                        return (float) (clientTick % threshold) / threshold;
                                    }))
                                    .layout(layout -> layout.width(PROGRESS_ARROW_WIDTH))))
                    .addChild(outputContainer));

            root.addChild(new UIElement().layout(layout -> layout.marginTop(3))
                    .addChild(new CLabel().setText(Component.translatable("xei.clayium.tier", this.tier)))
                    .addChild(new CLabel()
                            .setText(Component.translatable("xei.clayium.items_per_tick", this.itemPerTick)))
                    .addChild(new CLabel().setText(String.format("%s/t", CEUtils.formatCE(this.cePerTick)))));

            return new ModularUI(
                    UI.of(root, List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC))));
        }
    }
}
