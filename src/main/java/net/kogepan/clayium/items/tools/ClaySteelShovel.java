package net.kogepan.clayium.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public final class ClaySteelShovel extends ShovelItem implements ClaySteelTool {

    public ClaySteelShovel(Properties properties) {
        super(ClayToolMaterials.CLAY_STEEL, 4.0F, -3.0F, properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity owner) {
        boolean mined = super.mineBlock(stack, level, state, pos, owner);
        if (level instanceof ServerLevel serverLevel && owner instanceof ServerPlayer player) {
            ClaySteelToolMining.queueAdditionalBlocks(stack, serverLevel, pos, player);
        }
        return mined;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        boolean secondaryUse = player != null && player.isSecondaryUseActive();
        if (secondaryUse) {
            InteractionResult configurationResult = ClaySteelToolMining.configure(context.getItemInHand(), context);
            if (configurationResult != InteractionResult.PASS) {
                return configurationResult;
            }
        }

        InteractionResult shovelResult = super.useOn(context);
        if (shovelResult != InteractionResult.PASS) {
            return shovelResult;
        }
        return secondaryUse ? InteractionResult.PASS :
                ClaySteelToolMining.configure(context.getItemInHand(), context);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(
                                ItemStack stack,
                                TooltipContext context,
                                TooltipDisplay display,
                                Consumer<Component> builder,
                                TooltipFlag tooltipFlag) {
        ClaySteelToolMining.appendTooltip(stack, "item.clayium.clay_steel_shovel.tooltip", builder);
    }
}
