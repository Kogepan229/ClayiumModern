package net.kogepan.clayium.items.tools;

import net.kogepan.clayium.registries.ClayiumTags;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

public class ClayShovel extends ShovelItem {

    public ClayShovel(Properties properties) {
        super(ClayToolTiers.CLAY_SHOVEL,
                properties.attributes(ShovelItem.createAttributes(ClayToolTiers.CLAY_SHOVEL, 0.0f, -3.0f)));
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float speed = super.getDestroySpeed(stack, state);
        Tool tool = stack.get(DataComponents.TOOL);

        if (state.is(ClayiumTags.CLAY_TOOLS_BONUS_BLOCK) && tool != null && tool.isCorrectForDrops(state)) {
            return Math.max(32.0f, speed);
        }

        return speed;
    }
}
