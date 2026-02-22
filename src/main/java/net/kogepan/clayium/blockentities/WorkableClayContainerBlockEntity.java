package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.blockentities.trait.AbstractRecipeLogic;
import net.kogepan.clayium.blockentities.trait.AutoIOTrait;
import net.kogepan.clayium.blockentities.trait.ClayEnergyHolder;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.client.ldlib.textures.SlotTextures;
import net.kogepan.clayium.inventory.NotifiableItemStackHandler;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.slot.ItemHandlerSlot;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public abstract class WorkableClayContainerBlockEntity extends ClayContainerBlockEntity {

    protected final NotifiableItemStackHandler inputItemInventory;
    protected final NotifiableItemStackHandler outputItemInventory;

    protected final RecipeType<?> recipeType;
    protected final AbstractRecipeLogic recipeLogic;
    @Getter
    protected final ClayEnergyHolder energyHolder;

    public WorkableClayContainerBlockEntity(@NotNull BlockEntityType<?> type,
                                            @NotNull BlockPos pos,
                                            @NotNull BlockState blockState,
                                            @NotNull List<MachineIOMode> validInputModes,
                                            @NotNull List<MachineIOMode> validOutputModes,
                                            int inputSize,
                                            int outputSize,
                                            Function<WorkableClayContainerBlockEntity, @NotNull AbstractRecipeLogic> recipeLogicProvider,
                                            @NotNull RecipeType<?> recipeType) {
        super(type, pos, blockState, validInputModes, validOutputModes);
        this.inputItemInventory = new NotifiableItemStackHandler(this, inputSize, true);
        this.outputItemInventory = new NotifiableItemStackHandler(this, outputSize, false);
        this.recipeType = recipeType;
        this.energyHolder = new ClayEnergyHolder(this);
        this.recipeLogic = recipeLogicProvider.apply(this);

        this.addTrait(new AutoIOTrait.Combined(this, this.tier, false));
        this.addTrait(this.energyHolder);
        this.addTrait(this.recipeLogic);
    }

    @Override
    public void initDefaultRoutes() {
        this.inputModes.setMode(Direction.UP, MachineIOMode.ALL);
        BlockState blockState = this.getBlockState();
        if (blockState.getBlock() instanceof ClayContainerBlock containerBlock) {
            this.inputModes.setMode(blockState.getValue(containerBlock.getFacingProperty()).getOpposite(),
                    MachineIOMode.CE);
        }
        this.outputModes.setMode(Direction.DOWN, MachineIOMode.ALL);
    }

    @Override
    public IItemHandlerModifiable getInputInventory() {
        return this.inputItemInventory;
    }

    @Override
    public IItemHandlerModifiable getOutputInventory() {
        return this.outputItemInventory;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("inputItemInventory", this.inputItemInventory.serializeNBT(provider));
        tag.put("outputItemInventory", this.outputItemInventory.serializeNBT(provider));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("inputItemInventory")) {
            this.inputItemInventory.deserializeNBT(provider, tag.getCompound("inputItemInventory"));
        }
        if (tag.contains("outputItemInventory")) {
            this.outputItemInventory.deserializeNBT(provider, tag.getCompound("outputItemInventory"));
        }
    }

    private static final List<List<MachineIOMode>> VALID_INPUT_MODES_LISTS = List.of(
            List.of(MachineIOMode.NONE, MachineIOMode.CE),
            List.of(MachineIOMode.ALL, MachineIOMode.CE, MachineIOMode.NONE),
            List.of(MachineIOMode.ALL, MachineIOMode.FIRST, MachineIOMode.SECOND, MachineIOMode.CE,
                    MachineIOMode.NONE));

    private static final List<List<MachineIOMode>> VALID_OUTPUT_MODES_LISTS = List.of(
            List.of(MachineIOMode.NONE),
            List.of(MachineIOMode.ALL, MachineIOMode.NONE),
            List.of(MachineIOMode.ALL, MachineIOMode.FIRST, MachineIOMode.SECOND, MachineIOMode.NONE));

    public static List<MachineIOMode> getValidInputModes(int slots) {
        if (slots < VALID_INPUT_MODES_LISTS.size()) {
            return VALID_INPUT_MODES_LISTS.get(slots);
        }
        return VALID_INPUT_MODES_LISTS.getLast();
    }

    public static List<MachineIOMode> getValidOutputModes(int slots) {
        if (slots < VALID_OUTPUT_MODES_LISTS.size()) {
            return VALID_OUTPUT_MODES_LISTS.get(slots);
        }
        return VALID_OUTPUT_MODES_LISTS.getLast();
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        UIElement centerUI = new UIElement()
                .layout(layout -> layout.marginTop(8)
                        .marginBottom(4)
                        .display(TaffyDisplay.GRID)
                        .gridTemplateColumns("1fr auto 1fr")
                        .alignItems(AlignItems.CENTER)
                        .justifyContent(AlignContent.CENTER)
                        .gapColumn(8));

        centerUI.addChild(createInputSlots().layout(layout -> layout.gridColumn("1").justifySelf(AlignItems.END)))
                .addChild(this.recipeLogic.createProgressUIElement().layout(layout -> layout.gridColumn("2")))
                .addChild(createOutputSlots().layout(layout -> layout.gridColumn("3").justifySelf(AlignItems.START)));

        UIElement mainUI = new UIElement();
        mainUI.addChild(centerUI);
        if (this.tier < 3) {
            mainUI.addChild(new UIElement().layout(layout -> layout.height(0).alignItems(AlignItems.CENTER))
                    .addChild(this.energyHolder.createEnergyButtonElement()));
        }
        mainUI.addChild(this.energyHolder.createEnergyTextUIElement().textStyle(style -> style.adaptiveWidth(true)));

        root.addChild(mainUI);
    }

    protected UIElement createInputSlots() {
        UIElement inputSlotContainer = new UIElement().layout(layout -> layout.flexDirection(FlexDirection.ROW));
        if (this.inputItemInventory.getSlots() == 1) {
            inputSlotContainer.addChild(new ItemSlot().bind(new ItemHandlerSlot(this.inputItemInventory, 0)));
        } else if (this.inputItemInventory.getSlots() == 2) {
            inputSlotContainer.addChild(new ItemSlot().bind(new ItemHandlerSlot(this.inputItemInventory, 0))
                    .style(style -> style.backgroundTexture(SlotTextures.INPUT_SLOT_1)));
            inputSlotContainer.addChild(new ItemSlot().bind(new ItemHandlerSlot(this.inputItemInventory, 1))
                    .style(style -> style.backgroundTexture(SlotTextures.INPUT_SLOT_2)));
        }
        return inputSlotContainer;
    }

    protected UIElement createOutputSlots() {
        UIElement outputSlotContainer = new UIElement().layout(layout -> layout.flexDirection(FlexDirection.ROW));
        if (this.outputItemInventory.getSlots() == 1) {
            outputSlotContainer.addChild(
                    new ItemSlot().bind(new ItemHandlerSlot(this.outputItemInventory, 0).setCanPlace((s) -> false)));
        } else if (this.outputItemInventory.getSlots() == 2) {
            outputSlotContainer.addChild(
                    new ItemSlot().bind(new ItemHandlerSlot(this.outputItemInventory, 0).setCanPlace((s) -> false))
                            .style(style -> style.backgroundTexture(SlotTextures.OUTPUT_SLOT_1)));
            outputSlotContainer.addChild(
                    new ItemSlot().bind(new ItemHandlerSlot(this.outputItemInventory, 1).setCanPlace((s) -> false))
                            .style(style -> style.backgroundTexture(SlotTextures.OUTPUT_SLOT_2)));
        }
        return outputSlotContainer;
    }
}
