package net.kogepan.clayium.items;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.ISynchronizedInterface;
import net.kogepan.clayium.registries.ClayiumDataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public final class SynchronizerItem extends Item {

    public SynchronizerItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        ISynchronizedInterface synchronizedInterface = level.getCapability(
                ClayiumCapabilities.SYNCHRONIZED_INTERFACE,
                pos,
                state,
                blockEntity);
        if (synchronizedInterface != null) {
            return useOnInterface(stack, context, synchronizedInterface);
        }
        if (!(blockEntity instanceof ClayContainerBlockEntity container) ||
                !container.acceptsClayInterfaceSynchronization()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        GlobalPos target = GlobalPos.of(level.dimension(), pos.immutable());
        stack.set(ClayiumDataComponents.SYNCHRONIZER_TARGET.get(), target);
        player.sendOverlayMessage(createTargetMessage("item.clayium.synchronizer.saved", target));
        return InteractionResult.SUCCESS_SERVER;
    }

    private static InteractionResult useOnInterface(
                                                    ItemStack stack,
                                                    UseOnContext context,
                                                    ISynchronizedInterface targetInterface) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        GlobalPos savedTarget = stack.get(ClayiumDataComponents.SYNCHRONIZER_TARGET.get());
        if (savedTarget == null) {
            targetInterface.clearLinkedTarget();
            player.sendOverlayMessage(Component.translatable("item.clayium.synchronizer.unlinked"));
            return InteractionResult.SUCCESS_SERVER;
        }
        if (!targetInterface.canSynchronize()) {
            player.sendOverlayMessage(Component.translatable("item.clayium.synchronizer.requires_synchronous_parts"));
            return InteractionResult.SUCCESS_SERVER;
        }

        if (targetInterface.setLinkedTarget(savedTarget, ISynchronizedInterface.LinkSource.MANUAL)) {
            stack.remove(ClayiumDataComponents.SYNCHRONIZER_TARGET.get());
            player.sendOverlayMessage(createTargetMessage("item.clayium.synchronizer.linked", savedTarget));
        } else {
            player.sendOverlayMessage(createTargetMessage("item.clayium.synchronizer.link_failed", savedTarget));
        }
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
        GlobalPos target = stack.get(ClayiumDataComponents.SYNCHRONIZER_TARGET.get());
        if (target != null) {
            builder.accept(createTargetMessage("item.clayium.synchronizer.target", target));
        }
        builder.accept(Component.translatable("item.clayium.synchronizer.tooltip"));
    }

    private static Component createTargetMessage(String key, GlobalPos target) {
        return Component.translatable(
                key,
                target.dimension().identifier().toString(),
                target.pos().getX(),
                target.pos().getY(),
                target.pos().getZ());
    }
}
