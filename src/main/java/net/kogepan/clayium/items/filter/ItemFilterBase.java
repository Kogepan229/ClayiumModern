package net.kogepan.clayium.items.filter;

import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.items.TieredItem;
import net.kogepan.clayium.registries.ClayiumDataComponents;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import com.lowdragmc.lowdraglib2.gui.factory.HeldItemUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;

import java.util.function.Consumer;

public abstract class ItemFilterBase extends TieredItem implements HeldItemUIMenuType.HeldItemUI {

    protected ItemFilterBase(Item.Properties properties, int tier) {
        super(properties
                .stacksTo(1)
                .component(ClayiumDataComponents.FILTER_COPY_FLAG.get(), false), tier);
    }

    public abstract ItemFilterData createFilterData(ItemStack stack);

    @Override
    public abstract ModularUI createUI(HeldItemUIMenuType.HeldItemUIHolder holder);

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return FilterApplyHelper.applyFilterItemOnBlock(stack, context);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.descriptionId + (FilterItemHelper.hasCopyFlag(stack) ? ".copy" : ""));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(
                                ItemStack stack,
                                TooltipContext context,
                                TooltipDisplay display,
                                Consumer<Component> builder,
                                TooltipFlag tooltipFlag) {
        builder.accept(Component.translatable(this.descriptionId + ".tooltip"));
        super.appendHoverText(stack, context, display, builder, tooltipFlag);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (FilterItemHelper.hasCopyFlag(stack) && player.isSecondaryUseActive()) {
            if (!level.isClientSide()) {
                FilterItemHelper.setCopyFlag(stack, false);
                FilterItemHelper.setCopiedFilterData(stack, null);
                player.sendOverlayMessage(Component.translatable("message.clayium.item_filter.copy_cleared"));
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer serverPlayer && HeldItemUIMenuType.openUI(serverPlayer, hand)) {
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }
}
