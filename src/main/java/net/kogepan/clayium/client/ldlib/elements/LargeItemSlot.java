package net.kogepan.clayium.client.ldlib.elements;

import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.MCSprites;

import java.util.function.Consumer;

public class LargeItemSlot extends UIElement {

    private final ItemSlot itemSlot;

    public LargeItemSlot() {
        super();
        this.itemSlot = new ItemSlot();
        this.itemSlot.style(style -> style.backgroundTexture(IGuiTexture.EMPTY));

        this.getLayout().width(24).height(24).paddingAll(3);
        this.style(style -> style.backgroundTexture(MCSprites.RECT_1));
        this.addChild(this.itemSlot);
    }

    public LargeItemSlot itemSlot(Consumer<ItemSlot> itemSlotConsumer) {
        itemSlotConsumer.accept(this.itemSlot);
        return this;
    }
}
