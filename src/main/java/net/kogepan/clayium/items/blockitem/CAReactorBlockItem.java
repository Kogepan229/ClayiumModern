package net.kogepan.clayium.items.blockitem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Block item for a tiered CA Reactor core. */
public class CAReactorBlockItem extends TieredBlockItem {

    private static final int TOOLTIP_LINES = 10;

    public CAReactorBlockItem(Block block, Properties properties, int tier) {
        super(block, properties, tier);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        for (int line = 1; line <= TOOLTIP_LINES; line++) {
            tooltipComponents.add(Component.translatable("tooltip.clayium.ca_reactor." + line));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
