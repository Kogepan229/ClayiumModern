package net.kogepan.clayium.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClayCoreItem extends TieredItem {

    public ClayCoreItem(Properties properties) {
        super(properties, 8);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.clayium.clay_core.tooltip.line1")
                .withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("item.clayium.clay_core.tooltip.line2")
                .withStyle(ChatFormatting.GRAY));
    }
}
