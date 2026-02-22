package net.kogepan.clayium.items.filter;

import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

import org.jetbrains.annotations.NotNull;

/**
 * Shared block-use logic for items that apply/copy item filters.
 */
public final class FilterApplyHelper {

    private FilterApplyHelper() {}

    /**
     * Applies/copies a item filter using capabilities.
     */
    @NotNull
    public static InteractionResult applyFilterItemOnBlock(@NotNull ItemStack stack, @NotNull UseOnContext context) {
        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        var cap = ClayiumCapabilities.ITEM_FILTER_APPLICATABLE.getCapability(
                context.getLevel(),
                context.getClickedPos(),
                context.getLevel().getBlockState(context.getClickedPos()),
                context.getLevel().getBlockEntity(context.getClickedPos()),
                context.getClickedFace());
        if (cap == null) {
            return InteractionResult.PASS;
        }

        if (FilterItemHelper.hasCopyFlag(stack)) {
            ItemFilterData copied = cap.getFilter(context.getClickedFace());
            if (copied != null) {
                FilterItemHelper.setCopiedFilterData(stack, copied);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        ItemFilterData data = FilterItemHelper.getCopiedFilterData(stack);
        if (data == null) {
            data = stack.getCapability(ClayiumCapabilities.ITEM_FILTER_DATA);
        }
        if (data == null) {
            return InteractionResult.PASS;
        }

        cap.setFilter(context.getClickedFace(), data);
        return InteractionResult.SUCCESS;
    }
}
