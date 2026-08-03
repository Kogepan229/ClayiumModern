package net.kogepan.clayium.items;

import net.kogepan.clayium.api.configuration.ConfigurationToolItem;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.LevelReader;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Shared item behavior for Clay machine configurators. */
public class ClayConfiguratorItem extends ConfigurationToolItem {

    public ClayConfiguratorItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public boolean doesSneakBypassUse(@NotNull ItemStack stack, @NotNull LevelReader level, @NotNull BlockPos pos,
                                      @NotNull Player player) {
        return true;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
