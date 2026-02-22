package net.kogepan.clayium.items.filter;

import net.kogepan.clayium.capability.filter.data.ItemFilterData;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import com.lowdragmc.lowdraglib2.gui.factory.HeldItemUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Base class for filter items (Simple, Fuzzy, UnlocalizedName). Shared behaviour: stack size 1,
 * tooltip, copy flag for display name. Implements HeldItemUI for right-click to open GUI.
 */
public abstract class ItemFilterBase extends Item implements HeldItemUIMenuType.HeldItemUI {

    public ItemFilterBase() {
        super(new Properties().stacksTo(1));
    }

    /**
     * Creates serializable filter data from the current stack state.
     * This is used for applying filters to container sides and persisting them without storing raw ItemStack.
     */
    @Nullable
    public ItemFilterData createFilterData(@NotNull ItemStack stack) {
        return null;
    }

    /**
     * Creates the ModularUI for this filter item. Called when the player opens the filter GUI.
     */
    @Override
    @NotNull
    public ModularUI createUI(HeldItemUIMenuType.HeldItemUIHolder holder) {
        return createFilterUI(holder);
    }

    /**
     * Subclasses implement this to build the specific UI (slots, toggle, text field).
     */
    protected abstract ModularUI createFilterUI(HeldItemUIMenuType.HeldItemUIHolder holder);

    protected boolean hasCopyFlag(@NotNull ItemStack stack) {
        return FilterItemHelper.hasCopyFlag(stack);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return FilterApplyHelper.applyFilterItemOnBlock(stack, context);
    }

    @Override
    @NotNull
    public String getDescriptionId(@NotNull ItemStack stack) {
        String base = super.getDescriptionId(stack);
        return hasCopyFlag(stack) ? base + ".copy" : base;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipComponents, flag);
        String key = getDescriptionId(stack) + ".tooltip";
        tooltipComponents.add(Component.translatable(key));
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
                                                  @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            if (HeldItemUIMenuType.openUI(serverPlayer, hand)) {
                return InteractionResultHolder.success(stack);
            }
        }
        return InteractionResultHolder.pass(stack);
    }
}
