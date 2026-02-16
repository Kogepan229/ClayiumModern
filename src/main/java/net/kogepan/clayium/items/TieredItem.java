package net.kogepan.clayium.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TieredItem extends Item {

    private final int tier;

    public TieredItem(Properties properties, int tier) {
        super(setRarity(properties, tier));
        this.tier = tier;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                List<Component> tooltipComponents,
                                @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.clayium.tier", this.tier));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    private static Properties setRarity(Properties properties, int tier) {
        switch (tier) {
            case 4, 5, 6, 7 -> properties.rarity(Rarity.UNCOMMON);
            case 8, 9, 10, 11 -> properties.rarity(Rarity.RARE);
            case 12, 13 -> properties.rarity(Rarity.EPIC);
        }
        return properties;
    }
}
