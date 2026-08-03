package net.kogepan.clayium.items.tools;

import net.kogepan.clayium.registries.ClayiumTags;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public final class ClayPickaxe extends Item {

    public ClayPickaxe(Properties properties) {
        super(properties.pickaxe(ClayToolMaterials.CLAY_PICKAXE, 3.0F, -2.8F));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(ClayiumTags.CLAY_ORES) && this.isCorrectToolForDrops(stack, state)) {
            return 32.0F;
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(
                                ItemStack stack,
                                TooltipContext context,
                                TooltipDisplay display,
                                Consumer<Component> builder,
                                TooltipFlag tooltipFlag) {
        builder.accept(Component.translatable("item.clayium.clay_pickaxe.tooltip"));
    }
}
