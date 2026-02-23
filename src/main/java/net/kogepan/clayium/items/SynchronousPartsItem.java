package net.kogepan.clayium.items;

import net.kogepan.clayium.blockentities.ClayInterfaceBlockEntity;
import net.kogepan.clayium.capability.ISynchronizedInterface;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Synchronous parts item used to enable manual synchronizer linking on Clay Interfaces.
 */
public class SynchronousPartsItem extends TieredItem {

    public SynchronousPartsItem(Properties properties) {
        super(properties, 9);
    }

    @Override
    public InteractionResult onItemUseFirst(@NotNull ItemStack stack, @NotNull UseOnContext context) {
        var level = context.getLevel();
        var clickedBlockEntity = level.getBlockEntity(context.getClickedPos());
        if (!(clickedBlockEntity instanceof ClayInterfaceBlockEntity)) {
            return InteractionResult.PASS;
        }
        if (!(clickedBlockEntity instanceof ISynchronizedInterface synchronizedInterface)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        if (synchronizedInterface.canSynchronize()) {
            player.displayClientMessage(Component.translatable("item.clayium.synchronous_parts.already_inserted"),
                    true);
            return InteractionResult.SUCCESS;
        }

        synchronizedInterface.setCanSynchronize(true);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        player.displayClientMessage(Component.translatable("item.clayium.synchronous_parts.inserted"), true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.clayium.synchronous_parts.tooltip"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
