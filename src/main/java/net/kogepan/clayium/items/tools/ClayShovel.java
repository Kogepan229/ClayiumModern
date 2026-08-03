package net.kogepan.clayium.items.tools;

import net.kogepan.clayium.registries.ClayiumTags;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public final class ClayShovel extends ShovelItem {

    public ClayShovel(Properties properties) {
        super(ClayToolMaterials.CLAY_SHOVEL, 1.0F, -3.0F, properties);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(ClayiumTags.CLAY_SHOVEL_MINEABLE_ORES)) {
            return 12.0F;
        }
        if (state.is(ClayiumTags.CLAY_SHOVEL_FAST_BLOCKS)) {
            return 32.0F;
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.is(ClayiumTags.CLAY_SHOVEL_MINEABLE_ORES) || super.isCorrectToolForDrops(stack, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(
                                ItemStack stack,
                                TooltipContext context,
                                TooltipDisplay display,
                                Consumer<Component> builder,
                                TooltipFlag tooltipFlag) {
        builder.accept(Component.translatable("item.clayium.clay_shovel.tooltip"));
    }
}
