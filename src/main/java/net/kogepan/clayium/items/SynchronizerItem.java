package net.kogepan.clayium.items;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.ClayInterfaceBlockEntity;
import net.kogepan.clayium.capability.ISynchronizedInterface;
import net.kogepan.clayium.registries.ClayiumDataComponents;

import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Synchronizer item for linking Clay Interfaces to Clay Containers.
 */
public class SynchronizerItem extends Item {

    public SynchronizerItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult onItemUseFirst(@NotNull ItemStack stack, @NotNull UseOnContext context) {
        var level = context.getLevel();
        var clickedPos = context.getClickedPos();
        var clickedBlockEntity = level.getBlockEntity(clickedPos);

        boolean isContainerTarget = clickedBlockEntity instanceof ClayContainerBlockEntity &&
                !(clickedBlockEntity instanceof ClayInterfaceBlockEntity);
        boolean isInterfaceTarget = clickedBlockEntity instanceof ClayInterfaceBlockEntity;
        if (!isContainerTarget && !isInterfaceTarget) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        if (isContainerTarget) {
            GlobalPos savedTarget = GlobalPos.of(level.dimension(), clickedPos.immutable());
            stack.set(ClayiumDataComponents.SYNCHRONIZER_TARGET.get(), savedTarget);
            player.displayClientMessage(createTargetMessage("item.clayium.synchronizer.saved", savedTarget), true);
            return InteractionResult.SUCCESS;
        }

        if (isInterfaceTarget && clickedBlockEntity instanceof ClayInterfaceBlockEntity targetInterface) {
            GlobalPos savedTarget = stack.get(ClayiumDataComponents.SYNCHRONIZER_TARGET.get());
            if (savedTarget == null) {
                targetInterface.clearLinkedTarget();
                player.displayClientMessage(Component.translatable("item.clayium.synchronizer.unlinked"), true);
                return InteractionResult.SUCCESS;
            }

            boolean linked = targetInterface.setLinkedTarget(savedTarget, ISynchronizedInterface.LinkSource.MANUAL);
            if (linked) {
                stack.remove(ClayiumDataComponents.SYNCHRONIZER_TARGET.get());
                player.displayClientMessage(createTargetMessage("item.clayium.synchronizer.linked", savedTarget), true);
            } else {
                player.displayClientMessage(createTargetMessage("item.clayium.synchronizer.link_failed", savedTarget),
                        true);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        GlobalPos savedTarget = stack.get(ClayiumDataComponents.SYNCHRONIZER_TARGET.get());
        if (savedTarget != null) {
            tooltipComponents.add(createTargetMessage("item.clayium.synchronizer.target", savedTarget));
        }
        tooltipComponents.add(Component.translatable("item.clayium.synchronizer.tooltip"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @NotNull
    private static Component createTargetMessage(@NotNull String key, @NotNull GlobalPos target) {
        return Component.translatable(key,
                target.dimension().location().toString(),
                target.pos().getX(),
                target.pos().getY(),
                target.pos().getZ());
    }
}
