package net.kogepan.clayium.items.blockitem;

import net.kogepan.clayium.blocks.ResonatorBlock;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

/** Block item for a Resonator with its resonance factor in the tooltip. */
public class ResonatorBlockItem extends TieredBlockItem {

    private final double resonance;

    public ResonatorBlockItem(ResonatorBlock block, Item.Properties properties) {
        super(block, properties, block.getTier());
        this.resonance = block.getResonance();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.clayium.resonator.1"));
        tooltipComponents.add(Component.translatable("tooltip.clayium.resonator.2"));
        tooltipComponents.add(Component.translatable("tooltip.clayium.resonator.factor",
                String.format(Locale.ROOT, "%.3g", this.resonance)));
    }
}
