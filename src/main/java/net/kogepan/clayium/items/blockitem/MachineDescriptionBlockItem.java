package net.kogepan.clayium.items.blockitem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Tiered machine block item with a translated description. */
public class MachineDescriptionBlockItem extends TieredBlockItem {

    private final String descriptionKey;

    public MachineDescriptionBlockItem(Block block, Properties properties, int tier, String descriptionKey) {
        super(block, properties, tier);
        this.descriptionKey = descriptionKey;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(this.descriptionKey));
    }
}
