package net.kogepan.clayium.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public final class ClaySteelPickaxe extends Item implements ClaySteelTool {

    public ClaySteelPickaxe(Properties properties) {
        super(properties.pickaxe(ClayToolMaterials.CLAY_STEEL, 5.0F, -2.8F));
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
        return ClaySteelToolMining.configure(context.getItemInHand(), context);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(
                                ItemStack stack,
                                TooltipContext context,
                                TooltipDisplay display,
                                Consumer<Component> builder,
                                TooltipFlag tooltipFlag) {
        ClaySteelToolMining.appendTooltip(stack, "item.clayium.clay_steel_pickaxe.tooltip", builder);
    }
}
