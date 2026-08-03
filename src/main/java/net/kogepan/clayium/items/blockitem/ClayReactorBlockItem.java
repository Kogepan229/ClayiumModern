package net.kogepan.clayium.items.blockitem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClayReactorBlockItem extends TieredBlockItem {

    private static final int TOOLTIP_LINES = 9;

    public ClayReactorBlockItem(Block block, Properties properties) {
        super(block, properties, 7);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        for (int line = 1; line <= TOOLTIP_LINES; line++) {
            tooltipComponents.add(Component.translatable("tooltip.clayium.clay_reactor." + line));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
