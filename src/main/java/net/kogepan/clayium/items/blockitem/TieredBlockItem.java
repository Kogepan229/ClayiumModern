package net.kogepan.clayium.items.blockitem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class TieredBlockItem extends BlockItem {

    private final int tier;

    public TieredBlockItem(Block block, Properties properties, int tier) {
        super(block, applyRarity(properties, tier));
        this.tier = tier;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(
                                ItemStack stack,
                                TooltipContext context,
                                TooltipDisplay display,
                                Consumer<Component> builder,
                                TooltipFlag tooltipFlag) {
        builder.accept(Component.translatable("tooltip.clayium.tier", this.tier));
    }

    private static Properties applyRarity(Properties properties, int tier) {
        return switch (tier) {
            case 4, 5, 6, 7 -> properties.rarity(Rarity.UNCOMMON);
            case 8, 9, 10, 11 -> properties.rarity(Rarity.RARE);
            case 12, 13 -> properties.rarity(Rarity.EPIC);
            default -> properties;
        };
    }
}
