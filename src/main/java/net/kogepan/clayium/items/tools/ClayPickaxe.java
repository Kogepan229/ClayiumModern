package net.kogepan.clayium.items.tools;

import net.kogepan.clayium.registries.ClayiumTags;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

public class ClayPickaxe extends PickaxeItem {

    public ClayPickaxe(Properties properties) {
        super(ClayToolTiers.CLAY_PICKAXE,
                properties.attributes(PickaxeItem.createAttributes(ClayToolTiers.CLAY_PICKAXE, 2.0f, -2.8f)));
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float speed = super.getDestroySpeed(stack, state);
        Tool tool = stack.get(DataComponents.TOOL);

        if (state.is(ClayiumTags.CLAY_TOOLS_BONUS_BLOCK) && tool != null && tool.isCorrectForDrops(state)) {
            return speed * 10.0f / 3.0f;
        }

        return speed;
    }
}
