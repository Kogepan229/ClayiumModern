package net.kogepan.clayium.items.tools;

import net.kogepan.clayium.registries.ClayiumTags;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClayPickaxe extends PickaxeItem {

    public ClayPickaxe(Properties properties) {
        super(ClayToolTiers.CLAY_PICKAXE,
                properties.attributes(PickaxeItem.createAttributes(ClayToolTiers.CLAY_PICKAXE, 3.0f, -2.8f)));
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float speed = super.getDestroySpeed(stack, state);
        Tool tool = stack.get(DataComponents.TOOL);

        if (state.is(ClayiumTags.CLAY_ORES) && tool != null && tool.isCorrectForDrops(state)) {
            return 32.0f;
        }

        return speed;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }
}
