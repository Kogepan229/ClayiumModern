package net.kogepan.clayium.items.filter;

import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.IItemFilterApplicatable;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class FilterApplyHelper {

    private FilterApplyHelper() {}

    public static InteractionResult applyFilterItemOnBlock(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        IItemFilterApplicatable target = level.getCapability(
                ClayiumCapabilities.ITEM_FILTER_APPLICATABLE,
                pos,
                state,
                level.getBlockEntity(pos),
                context.getClickedFace());
        if (target == null) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        if (FilterItemHelper.hasCopyFlag(stack)) {
            ItemFilterData copied = target.getFilter(context.getClickedFace());
            if (copied == null) {
                return InteractionResult.PASS;
            }
            FilterItemHelper.setCopiedFilterData(stack, copied);
            if (player != null) {
                player.sendOverlayMessage(Component.translatable("message.clayium.item_filter.copied"));
            }
            return InteractionResult.SUCCESS_SERVER;
        }

        ItemFilterData data = FilterItemHelper.getCopiedFilterData(stack);
        if (data == null) {
            data = stack.getCapability(ClayiumCapabilities.ITEM_FILTER_DATA);
        }
        if (data == null) {
            return InteractionResult.PASS;
        }

        target.setFilter(context.getClickedFace(), data);
        if (player != null) {
            player.sendOverlayMessage(Component.translatable("message.clayium.item_filter.applied"));
        }
        return InteractionResult.SUCCESS_SERVER;
    }
}
