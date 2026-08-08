package net.kogepan.clayium.items;

import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.ISynchronizedInterface;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public final class SynchronousPartsItem extends TieredItem {

    public SynchronousPartsItem(Properties properties) {
        super(properties, 9);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ISynchronizedInterface target = level.getCapability(
                ClayiumCapabilities.SYNCHRONIZED_INTERFACE,
                pos,
                state,
                level.getBlockEntity(pos));
        if (target == null) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (target.canSynchronize()) {
            player.sendOverlayMessage(Component.translatable("item.clayium.synchronous_parts.already_inserted"));
            return InteractionResult.SUCCESS_SERVER;
        }

        target.setCanSynchronize(true);
        stack.consume(1, player);
        player.sendOverlayMessage(Component.translatable("item.clayium.synchronous_parts.inserted"));
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(
                                ItemStack stack,
                                TooltipContext context,
                                TooltipDisplay display,
                                Consumer<Component> builder,
                                TooltipFlag tooltipFlag) {
        builder.accept(Component.translatable("item.clayium.synchronous_parts.tooltip"));
        super.appendHoverText(stack, context, display, builder, tooltipFlag);
    }
}
