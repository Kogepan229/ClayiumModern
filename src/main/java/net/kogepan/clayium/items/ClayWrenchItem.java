package net.kogepan.clayium.items;

import net.kogepan.clayium.api.configuration.ConfigurationToolItem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Rotates Clay machines and other blocks that expose a rotatable block state. */
public class ClayWrenchItem extends ConfigurationToolItem {

    public ClayWrenchItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.clayium.clay_wrench.tooltip"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
