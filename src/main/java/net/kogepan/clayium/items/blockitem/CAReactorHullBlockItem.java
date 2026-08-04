package net.kogepan.clayium.items.blockitem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Block item for a ranked CA Reactor hull. */
public class CAReactorHullBlockItem extends TieredBlockItem {

    private final int rank;

    public CAReactorHullBlockItem(Block block, Properties properties, int tier, int rank) {
        super(block, properties, tier);
        this.rank = rank;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.clayium.ca_reactor_hull." + this.rank));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
