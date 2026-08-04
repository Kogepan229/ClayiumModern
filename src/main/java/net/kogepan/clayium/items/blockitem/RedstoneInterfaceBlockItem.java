package net.kogepan.clayium.items.blockitem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Block item for a tiered Redstone Interface. */
public class RedstoneInterfaceBlockItem extends TieredBlockItem {

    public RedstoneInterfaceBlockItem(Block block, Properties properties, int tier) {
        super(block, properties, tier);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.clayium.redstone_interface.1"));
        tooltipComponents.add(Component.translatable("tooltip.clayium.redstone_interface.2"));
        tooltipComponents.add(Component.translatable("tooltip.clayium.redstone_interface.3"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
