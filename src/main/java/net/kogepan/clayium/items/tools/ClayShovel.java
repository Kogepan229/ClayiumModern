package net.kogepan.clayium.items.tools;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TooltipFlag;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClayShovel extends ShovelItem {

    public ClayShovel(Properties properties) {
        super(ClayToolTiers.CLAY_SHOVEL,
                properties.attributes(ShovelItem.createAttributes(ClayToolTiers.CLAY_SHOVEL, 1.0f, -3.0f)));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }
}
