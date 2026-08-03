package net.kogepan.clayium.items;

import net.kogepan.clayium.api.configuration.ConfigurationToolItem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public final class ClayConfigurationToolItem extends ConfigurationToolItem {

    public ClayConfigurationToolItem(Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(
                                ItemStack stack,
                                TooltipContext context,
                                TooltipDisplay display,
                                Consumer<Component> builder,
                                TooltipFlag tooltipFlag) {
        builder.accept(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }
}
