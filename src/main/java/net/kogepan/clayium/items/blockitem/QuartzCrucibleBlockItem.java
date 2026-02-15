package net.kogepan.clayium.items.blockitem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Block item for Quartz Crucible that shows usage tooltip. */
public class QuartzCrucibleBlockItem extends BlockItem {

    public QuartzCrucibleBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.clayium.quartz_crucible"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
