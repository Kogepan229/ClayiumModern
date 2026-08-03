package net.kogepan.clayium.items.blockitem;

import net.kogepan.clayium.blocks.OverclockerBlock;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

/** Block item for an Overclocker with its acceleration factor in the tooltip. */
public class OverclockerBlockItem extends TieredBlockItem {

    private final double overclockFactor;

    public OverclockerBlockItem(OverclockerBlock block, Item.Properties properties) {
        super(block, properties, block.getTier());
        this.overclockFactor = block.getOverclockFactor();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.clayium.overclocker"));
        tooltipComponents.add(Component.translatable("tooltip.clayium.overclocker.factor",
                String.format(Locale.ROOT, "%.3g", this.overclockFactor)));
    }
}
