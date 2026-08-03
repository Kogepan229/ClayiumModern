package net.kogepan.clayium.items.gadget.behavior;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.api.gadget.ClayGadgetCategories;
import net.kogepan.clayium.api.gadget.ClayGadgetContext;
import net.kogepan.clayium.api.gadget.IClayGadget;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

public record LongArmGadget(double amount, int priority) implements IClayGadget {

    private static final ResourceLocation BLOCK_MODIFIER_ID = Clayium.id("gadget_long_arm_block");
    private static final ResourceLocation ENTITY_MODIFIER_ID = Clayium.id("gadget_long_arm_entity");

    @Override
    @NotNull
    public ResourceLocation getCategory(@NotNull ItemStack stack) {
        return ClayGadgetCategories.LONG_ARM;
    }

    @Override
    public int getPriority(@NotNull ItemStack stack) {
        return priority;
    }

    @Override
    public void onActivated(@NotNull ClayGadgetContext context) {
        apply(context);
    }

    @Override
    public void onDeactivated(@NotNull ClayGadgetContext context) {
        remove(context, Attributes.BLOCK_INTERACTION_RANGE, BLOCK_MODIFIER_ID);
        remove(context, Attributes.ENTITY_INTERACTION_RANGE, ENTITY_MODIFIER_ID);
    }

    private void apply(ClayGadgetContext context) {
        apply(context, Attributes.BLOCK_INTERACTION_RANGE, BLOCK_MODIFIER_ID);
        apply(context, Attributes.ENTITY_INTERACTION_RANGE, ENTITY_MODIFIER_ID);
    }

    private void apply(ClayGadgetContext context,
                       net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attributeType,
                       ResourceLocation modifierId) {
        AttributeInstance attribute = context.player().getAttribute(attributeType);
        if (attribute != null) {
            attribute.addOrUpdateTransientModifier(
                    new AttributeModifier(modifierId, amount, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static void remove(ClayGadgetContext context,
                               net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attributeType,
                               ResourceLocation modifierId) {
        AttributeInstance attribute = context.player().getAttribute(attributeType);
        if (attribute != null) {
            attribute.removeModifier(modifierId);
        }
    }
}
