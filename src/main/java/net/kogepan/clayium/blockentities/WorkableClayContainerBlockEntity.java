package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blockentities.trait.AbstractRecipeLogic;
import net.kogepan.clayium.blockentities.trait.ClayEnergyHolder;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.inventory.ClayiumItemStorage;
import net.kogepan.clayium.recipes.SimpleMachineRecipeType;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.utils.CEUtils;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.slot.ItemResourceHandlerSlot;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ProgressBar;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;

import java.util.List;

public abstract class WorkableClayContainerBlockEntity extends ClayContainerBlockEntity {

    private static final String INPUT_INVENTORY_KEY = "inputItemInventory";
    private static final String OUTPUT_INVENTORY_KEY = "outputItemInventory";
    private static final long MANUAL_ENERGY_AMOUNT = CEUtils.TEN_MICRO_CE * 5L;

    protected final ClayiumItemStorage inputItemInventory;
    protected final ClayiumItemStorage outputItemInventory;
    protected final ClayEnergyHolder energyHolder;
    protected final AbstractRecipeLogic recipeLogic;

    protected WorkableClayContainerBlockEntity(
                                               BlockEntityType<?> type,
                                               BlockPos pos,
                                               BlockState blockState,
                                               SimpleMachineRecipeType<MachineRecipe> recipeType,
                                               AbstractRecipeLogicFactory recipeLogicFactory) {
        super(
                type,
                pos,
                blockState,
                validInputModes(recipeType.maxInputSize()),
                validOutputModes(recipeType.maxOutputSize()));
        this.inputItemInventory = new ClayiumItemStorage(
                recipeType.maxInputSize(),
                (index, previousContents) -> this.notifyItemInputInventoryChanged());
        this.outputItemInventory = new ClayiumItemStorage(
                recipeType.maxOutputSize(),
                (index, previousContents) -> this.notifyItemOutputInventoryChanged());
        this.energyHolder = new ClayEnergyHolder(this);
        this.recipeLogic = recipeLogicFactory.create(this, recipeType);
        this.addTrait(this.energyHolder);
        this.addTrait(this.recipeLogic);
    }

    @Override
    public void initDefaultRoutes() {
        this.inputModes.setMode(Direction.UP, MachineIOMode.ALL);
        Direction rear = this.getBlockState().getValue(ClayContainerBlock.FACING).getOpposite();
        this.inputModes.setMode(rear, MachineIOMode.CE);
        this.outputModes.setMode(Direction.DOWN, MachineIOMode.ALL);
    }

    public final ClayEnergyHolder energyHolder() {
        return this.energyHolder;
    }

    @Override
    public final ResourceHandler<ItemResource> getInputInventory() {
        return this.inputItemInventory;
    }

    @Override
    public final ResourceHandler<ItemResource> getOutputInventory() {
        return this.outputItemInventory;
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        UIElement machineRow = new UIElement().layout(layout -> layout
                .flexDirection(FlexDirection.ROW)
                .alignItems(AlignItems.CENTER)
                .justifyContent(AlignContent.CENTER)
                .marginTop(8)
                .marginBottom(4));

        machineRow.addChild(this.createInputSlots().layout(layout -> layout.marginRight(8)));
        machineRow.addChild(this.createProgressBar().layout(layout -> layout.marginRight(8)));
        machineRow.addChild(this.createOutputSlots());
        root.addChild(machineRow);

        UIElement energyRow = new UIElement().layout(layout -> layout
                .flexDirection(FlexDirection.ROW)
                .alignItems(AlignItems.CENTER)
                .justifyContent(AlignContent.CENTER)
                .widthPercent(100));
        if (this.tier() < 3) {
            energyRow.addChild(this.createEnergyButton().layout(layout -> layout.marginRight(4)));
        }
        Label energyLabel = new Label();
        energyLabel.bind(DataBindingBuilder.componentS2C(() -> Component.translatable(
                "gui.clayium.machine.energy",
                CEUtils.formatCE(this.energyHolder.getAmountAsLong())))
                .build());
        energyRow.addChild(energyLabel);
        root.addChild(energyRow);
    }

    private UIElement createInputSlots() {
        UIElement slots = new UIElement().layout(layout -> layout.flexDirection(FlexDirection.ROW));
        for (int index = 0; index < this.inputItemInventory.size(); index++) {
            slots.addChild(new ItemSlot().bind(this.inputItemInventory, index));
        }
        return slots;
    }

    private UIElement createOutputSlots() {
        UIElement slots = new UIElement().layout(layout -> layout.flexDirection(FlexDirection.ROW));
        for (int index = 0; index < this.outputItemInventory.size(); index++) {
            ItemResourceHandlerSlot slot = new ItemResourceHandlerSlot(this.outputItemInventory, index)
                    .setCanPlace(stack -> false);
            slots.addChild(new ItemSlot().bind(slot));
        }
        return slots;
    }

    private ProgressBar createProgressBar() {
        SpriteTexture emptyArrow = SpriteTexture.of(Clayium.id("textures/gui/progress_bar_arrow.png"))
                .setSprite(0, 0, 20, 20)
                .setBorder(1, 0, 8, 0);
        SpriteTexture filledArrow = SpriteTexture.of(Clayium.id("textures/gui/progress_bar_arrow.png"))
                .setSprite(0, 20, 20, 20)
                .setBorder(1, 0, 8, 0);
        ProgressBar progressBar = new ProgressBar();
        progressBar.layout(layout -> layout.width(22).height(20));
        progressBar.barContainer(container -> container
                .layout(layout -> layout.paddingAll(0))
                .style(style -> style.backgroundTexture(emptyArrow)));
        progressBar.bar(bar -> bar.style(style -> style.backgroundTexture(filledArrow)));
        progressBar.label(label -> label.setText(Component.empty()));
        progressBar.style(style -> style.tooltips(Component.translatable("gui.clayium.machine.progress")));
        progressBar.bind(DataBindingBuilder.floatValS2C(this.recipeLogic::progressFraction).build());
        return progressBar;
    }

    private Button createEnergyButton() {
        SpriteTexture base = SpriteTexture.of(Clayium.id("textures/gui/buttons.png"))
                .setSprite(0, 16, 16, 16);
        SpriteTexture hovered = SpriteTexture.of(Clayium.id("textures/gui/buttons.png"))
                .setSprite(0, 32, 16, 16);
        Button button = new Button().noText();
        button.layout(layout -> layout.width(16).height(16).paddingAll(0));
        button.getButtonStyle()
                .baseTexture(base)
                .hoverTexture(hovered)
                .pressedTexture(hovered);
        button.style(style -> style.tooltips(Component.translatable(
                "gui.clayium.machine.add_energy",
                CEUtils.formatCE(MANUAL_ENERGY_AMOUNT))));
        button.setOnServerClick(event -> this.recipeLogic.tryAddManualEnergy(MANUAL_ENERGY_AMOUNT));
        return button;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild(INPUT_INVENTORY_KEY, this.inputItemInventory);
        output.putChild(OUTPUT_INVENTORY_KEY, this.outputItemInventory);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.inputItemInventory.deserialize(input.childOrEmpty(INPUT_INVENTORY_KEY));
        this.outputItemInventory.deserialize(input.childOrEmpty(OUTPUT_INVENTORY_KEY));
    }

    private static List<MachineIOMode> validInputModes(int slots) {
        return switch (slots) {
            case 0 -> List.of(MachineIOMode.NONE, MachineIOMode.CE);
            case 1 -> List.of(MachineIOMode.ALL, MachineIOMode.CE, MachineIOMode.NONE);
            default -> List.of(
                    MachineIOMode.ALL,
                    MachineIOMode.FIRST,
                    MachineIOMode.SECOND,
                    MachineIOMode.CE,
                    MachineIOMode.NONE);
        };
    }

    private static List<MachineIOMode> validOutputModes(int slots) {
        return switch (slots) {
            case 0 -> List.of(MachineIOMode.NONE);
            case 1 -> List.of(MachineIOMode.ALL, MachineIOMode.NONE);
            default -> List.of(
                    MachineIOMode.ALL,
                    MachineIOMode.FIRST,
                    MachineIOMode.SECOND,
                    MachineIOMode.NONE);
        };
    }

    @FunctionalInterface
    protected interface AbstractRecipeLogicFactory {

        AbstractRecipeLogic create(
                                   WorkableClayContainerBlockEntity blockEntity,
                                   SimpleMachineRecipeType<MachineRecipe> recipeType);
    }
}
