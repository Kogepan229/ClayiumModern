package net.kogepan.clayium.items.blockitem;

import net.kogepan.clayium.blockentities.AbstractFilteredContainerBlockEntity;
import net.kogepan.clayium.blockentities.StorageContainerBlockEntity;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StorageContainerBlockItem extends TieredBlockItem {

    public StorageContainerBlockItem(Block block, Properties properties) {
        super(block, properties, 6);
    }

    public static int getCapacity(@NotNull ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
        return tag.contains(StorageContainerBlockEntity.CAPACITY_TAG) ?
                Math.max(1, tag.getInt(StorageContainerBlockEntity.CAPACITY_TAG)) :
                StorageContainerBlockEntity.DEFAULT_CAPACITY;
    }

    public static boolean isUpgraded(@NotNull ItemStack stack) {
        return getCapacity(stack) == StorageContainerBlockEntity.MAX_CAPACITY;
    }

    public static void setCapacity(@NotNull ItemStack stack, int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Storage Container capacity must be positive");
        }
        CompoundTag tag = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
        tag.putInt(StorageContainerBlockEntity.CAPACITY_TAG, capacity);
        BlockItem.setBlockEntityData(stack, ClayiumBlockEntityTypes.STORAGE_CONTAINER_BLOCK_ENTITY.get(), tag);
    }

    @NotNull
    public static ItemStack createWithCapacity(int capacity) {
        ItemStack stack = new ItemStack(ClayiumBlocks.STORAGE_CONTAINER.get());
        setCapacity(stack, capacity);
        return stack;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        HolderLookup.Provider provider = context.registries();
        if (provider == null) {
            return;
        }
        CompoundTag tag = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
        ItemStack displayItem = ItemStack.parseOptional(provider,
                tag.getCompound(StorageContainerBlockEntity.STORED_ITEM_TAG));
        int amount = Math.min(Math.max(0, tag.getInt(StorageContainerBlockEntity.STORED_AMOUNT_TAG)),
                getCapacity(stack));
        if (displayItem.isEmpty()) {
            displayItem = ItemStack.parseOptional(provider,
                    tag.getCompound(AbstractFilteredContainerBlockEntity.GLOBAL_FILTER_TAG));
            amount = 0;
        }
        if (displayItem.isEmpty()) {
            return;
        }

        tooltipComponents.add(displayItem.getHoverName());
        tooltipComponents.add(Component.literal(amount + "/" + getCapacity(stack)).withStyle(ChatFormatting.GRAY));
    }
}
