package net.kogepan.clayium.items;

import net.kogepan.clayium.api.configuration.IMachineConfigurable;
import net.kogepan.clayium.api.configuration.MachineIOConfiguration;
import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.registries.ClayiumDataComponents;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Stores and restores Clay Container orientation and per-side IO settings.
 */
public class MemoryCardItem extends Item {

    public MemoryCardItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult onItemUseFirst(@NotNull ItemStack stack, @NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockState state = level.getBlockState(context.getClickedPos());
        IMachineConfigurable configurable = ClayiumCapabilities.MACHINE_CONFIGURABLE.getCapability(
                level, context.getClickedPos(), state, level.getBlockEntity(context.getClickedPos()), null);
        if (configurable == null || !configurable.supportsConfigurationMemory(context)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            MachineIOConfiguration capturedConfiguration = configurable.captureConfiguration(context);
            if (capturedConfiguration != null) {
                stack.set(ClayiumDataComponents.MACHINE_IO_CONFIGURATION.get(), capturedConfiguration);
                player.displayClientMessage(Component.translatable("item.clayium.memory_card.saved"), false);
            }
            return InteractionResult.SUCCESS;
        }

        MachineIOConfiguration configuration = stack.get(ClayiumDataComponents.MACHINE_IO_CONFIGURATION.get());
        if (configuration != null) {
            boolean applied = configurable.applyConfiguration(context, configuration);
            player.displayClientMessage(Component.translatable(applied ?
                    "item.clayium.memory_card.loaded" :
                    "item.clayium.memory_card.invalid"), false);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.clayium.memory_card.tooltip"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
