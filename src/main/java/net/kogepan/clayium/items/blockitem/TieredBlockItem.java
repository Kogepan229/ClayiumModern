package net.kogepan.clayium.items.blockitem;

import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.machine.replacement.MachineReplacementService;
import net.kogepan.clayium.registries.ClayiumAttachments;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TieredBlockItem extends BlockItem {

    private final int tier;

    public TieredBlockItem(Block block, Properties properties, int tier) {
        super(block, setRarity(properties, tier));
        this.tier = tier;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                List<Component> tooltipComponents,
                                @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.clayium.tier", this.tier));
        if (this.getBlock() instanceof ClayContainerBlock) {
            tooltipComponents.add(Component.translatable("tooltip.clayium.machine_replacement"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResult onItemUseFirst(@NotNull ItemStack stack, @NotNull UseOnContext context) {
        Player player = context.getPlayer();
        if (!(this.getBlock() instanceof ClayContainerBlock) || player == null ||
                !player.getData(ClayiumAttachments.SPRINT_KEY_ACTIVE) ||
                !(context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof ClayContainerBlock)) {
            return InteractionResult.PASS;
        }
        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        return MachineReplacementService.replace(context);
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
