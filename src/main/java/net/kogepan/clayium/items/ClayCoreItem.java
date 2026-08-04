package net.kogepan.clayium.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public final class ClayCoreItem extends TieredItem {

    public ClayCoreItem(Properties properties) {
        super(properties, 8);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(
                                ItemStack stack,
                                TooltipContext context,
                                TooltipDisplay display,
                                Consumer<Component> builder,
                                TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, builder, tooltipFlag);
        builder.accept(Component.translatable("item.clayium.clay_core.tooltip.line1")
                .withStyle(ChatFormatting.GRAY));
        builder.accept(Component.translatable("item.clayium.clay_core.tooltip.line2")
                .withStyle(ChatFormatting.GRAY));
    }
}
