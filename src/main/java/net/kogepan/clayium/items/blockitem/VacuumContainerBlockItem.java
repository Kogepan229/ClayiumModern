package net.kogepan.clayium.items.blockitem;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VacuumContainerBlockItem extends TieredBlockItem {

    public VacuumContainerBlockItem(Block block, Properties properties) {
        super(block, properties, 6);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("block.clayium.vacuum_container.description")
                .withStyle(ChatFormatting.GRAY));
    }
}
