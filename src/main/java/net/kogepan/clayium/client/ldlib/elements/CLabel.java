package net.kogepan.clayium.client.ldlib.elements;

import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;

public class CLabel extends Label {

    public CLabel() {
        super();
        this.textStyle(style -> style.textColor(0x404040).textShadow(false));
    }
}
