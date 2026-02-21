package net.kogepan.clayium.items.filter;

import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.IItemFilter;

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
     * Creates the IItemFilter instance from the current stack state. Used by the capability provider.
     */
    public abstract IItemFilter createFilter(@NotNull ItemStack stack);

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
        if (hasCopyFlag(stack)) {
            ItemStack created = cap.createFilterStack(context.getClickedFace());
            if (created != null) {
                FilterItemHelper.setCopyFlag(created, true);
                context.getPlayer().setItemInHand(context.getHand(), created);
                return InteractionResult.SUCCESS;
            }
        } else {
            cap.setFilter(context.getClickedFace(), createFilter(stack), stack.copy());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
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
