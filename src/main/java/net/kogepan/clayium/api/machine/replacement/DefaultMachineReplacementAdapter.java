package net.kogepan.clayium.api.machine.replacement;

import net.kogepan.clayium.blockentities.WorkableClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.machine.AbstractMultiblockMachineBlockEntity;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.recipes.recipes.WeightedMachineRecipe;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/** Default adapter for tier variants backed by one BlockEntityType. */
public final class DefaultMachineReplacementAdapter implements MachineReplacementAdapter {

    private final ResourceLocation familyId;

    public DefaultMachineReplacementAdapter(ResourceLocation familyId) {
        this.familyId = Objects.requireNonNull(familyId, "familyId");
    }

    @Override
    public ResourceLocation familyId() {
        return this.familyId;
    }

    @Override
    public MachineReplacementDecision canReplace(MachineReplacementContext context) {
        if (context.sourceState().is(context.targetState().getBlock())) {
            return MachineReplacementDecision.deny(
                    Component.translatable("message.clayium.machine_replacement.same_machine"));
        }
        if (!(context.source() instanceof WorkableClayContainerBlockEntity workable) ||
                !workable.getRecipeLogic().isProcessingRecipe()) {
            return MachineReplacementDecision.allow();
        }

        RecipeHolder<?> recipe = workable.getRecipeLogic().getProcessingRecipeHolder();
        Integer requiredTier = getRecipeTier(recipe);
        if (requiredTier != null && requiredTier > context.target().tier) {
            return MachineReplacementDecision.deny(
                    Component.translatable("message.clayium.machine_replacement.active_recipe_tier"));
        }
        if (requiredTier == null && context.target().tier < context.source().tier) {
            return MachineReplacementDecision.deny(
                    Component.translatable("message.clayium.machine_replacement.active_recipe_tier"));
        }
        return MachineReplacementDecision.allow();
    }

    @Override
    public MachineReplacementDecision validateInstalled(MachineReplacementContext context) {
        if (context.source() instanceof AbstractMultiblockMachineBlockEntity sourceMultiblock &&
                sourceMultiblock.getRecipeLogic().isProcessingRecipe() &&
                context.target() instanceof AbstractMultiblockMachineBlockEntity targetMultiblock &&
                !targetMultiblock.isStructureFormed()) {
            return MachineReplacementDecision.deny(
                    Component.translatable("message.clayium.machine_replacement.invalid_structure"));
        }
        return MachineReplacementDecision.allow();
    }

    @Nullable
    private static Integer getRecipeTier(@Nullable RecipeHolder<?> holder) {
        if (holder == null) {
            return null;
        }
        if (holder.value() instanceof MachineRecipe recipe) {
            return recipe.recipeTier();
        }
        if (holder.value() instanceof WeightedMachineRecipe recipe) {
            return recipe.recipeTier();
        }
        return null;
    }
}
