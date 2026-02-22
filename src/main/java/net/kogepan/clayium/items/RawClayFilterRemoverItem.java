package net.kogepan.clayium.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Raw clay tool that removes side filters from machines.
 */
public class RawClayFilterRemoverItem extends Item {

    public RawClayFilterRemoverItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.clayium.filter_remover.tooltip1"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
