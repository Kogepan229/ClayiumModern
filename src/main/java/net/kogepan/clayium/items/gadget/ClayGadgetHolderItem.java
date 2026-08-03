package net.kogepan.clayium.items.gadget;

import net.kogepan.clayium.client.ldlib.elements.CLabel;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import com.lowdragmc.lowdraglib2.gui.factory.HeldItemUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvent;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.FlexDirection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClayGadgetHolderItem extends Item implements HeldItemUIMenuType.HeldItemUI {

    public ClayGadgetHolderItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents,
                                @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.clayium.clay_gadget_holder.tooltip"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @NotNull
    public static ClayGadgetItemHandler createHandler(@NotNull ItemStack holder) {
        return new ClayGadgetItemHandler(holder);
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
                                                  @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }
        if (player instanceof ServerPlayer serverPlayer && HeldItemUIMenuType.openUI(serverPlayer, hand)) {
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    @NotNull
    public ModularUI createUI(HeldItemUIMenuType.HeldItemUIHolder holder) {
        ClayGadgetItemHandler handler = createHandler(holder.player.getItemInHand(holder.hand));
        var root = new UIElement().layout(layout -> layout.paddingAll(6).justifyContent(AlignContent.CENTER))
                .addClass("panel_bg");
        root.addChild(new CLabel().setText(holder.itemStack.getHoverName().getString()));
        var slots = new UIElement();
        for (int row = 0; row < 2; row++) {
            var slotRow = new UIElement().layout(
                    layout -> layout.flexDirection(FlexDirection.ROW).justifyContent(AlignContent.CENTER));
            slots.addChild(slotRow);
            for (int column = 0; column < 5; column++) {
                slotRow.addChild(new ItemSlot().bind(handler, row * 5 + column));
            }
        }
        root.addChild(slots);
        root.addChild(new CLabel().setText("container.inventory"));
        root.addChild(new LockedInventorySlots(holder.hand));
        return new ModularUI(UI.of(root, List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC))),
                holder.player);
    }

    private static class LockedInventorySlots extends InventorySlots {

        private final InteractionHand hand;

        LockedInventorySlots(InteractionHand hand) {
            this.hand = hand;
        }

        @Override
        protected void onModularUIChanged(UIEvent event) {
            super.onModularUIChanged(event);
            var modularUI = getModularUI();
            if (modularUI == null || modularUI.player == null || hand != InteractionHand.MAIN_HAND) {
                return;
            }
            var inventory = modularUI.player.getInventory();
            hotbar.slots[inventory.selected].bind(new Slot(inventory, inventory.selected, 0, 0) {

                @Override
                public boolean mayPickup(Player player) {
                    return false;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }
    }
}
