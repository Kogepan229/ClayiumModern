package net.kogepan.clayium.items.gadget;

import net.kogepan.clayium.api.gadget.IClayGadget;
import net.kogepan.clayium.items.TieredItem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClayGadgetItem extends TieredItem {

    private final IClayGadget gadget;
    private final boolean hasEffectTooltip;

    public ClayGadgetItem(Properties properties, int tier, @NotNull IClayGadget gadget) {
        this(properties, tier, gadget, false);
    }

    public ClayGadgetItem(Properties properties, int tier, @NotNull IClayGadget gadget,
                          boolean hasEffectTooltip) {
        super(properties.stacksTo(1), tier);
        this.gadget = gadget;
        this.hasEffectTooltip = hasEffectTooltip;
    }

    @NotNull
    public IClayGadget getGadget() {
        return gadget;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents,
                                @NotNull TooltipFlag tooltipFlag) {
        if (hasEffectTooltip) {
            tooltipComponents.add(Component.translatable(getDescriptionId(stack) + ".tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
