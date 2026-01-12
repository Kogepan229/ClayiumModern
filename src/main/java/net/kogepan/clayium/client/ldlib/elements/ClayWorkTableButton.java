package net.kogepan.clayium.client.ldlib.elements;

import net.kogepan.clayium.client.ldlib.textures.ClayWorkTableButtonTextures;

import com.lowdragmc.lowdraglib2.gui.sync.bindings.IBindable;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.IDataConsumer;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.IDataProvider;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEventListener;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.util.ITickable;
import com.lowdragmc.lowdraglib2.syncdata.ISubscription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClayWorkTableButton extends Button implements IBindable<Boolean>, IDataConsumer<Boolean> {

    private final ClayWorkTableButtonTextures.ButtonTexture texture;

    private final Map<IDataProvider<Boolean>, ISubscription> dataSources = new LinkedHashMap<>();

    public ClayWorkTableButton(ClayWorkTableButtonTextures.ButtonTexture texture) {
        super();
        noText();
        layout(l -> l.width(16).height(16));
        buttonStyle(style -> style
                .baseTexture(texture.base)
                .hoverTexture(texture.hovered)
                .pressedTexture(texture.hovered));

        this.texture = texture;
    }

    @Override
    public void drawBackgroundAdditional(@NotNull GUIContext guiContext) {
        if (this.isActive()) {
            super.drawBackgroundAdditional(guiContext);
        } else {
            guiContext.drawTexture(texture.disabled, getPositionX(), getPositionY(), getSizeWidth(),
                    getSizeHeight());
        }
    }

    @Override
    public UIElement bindDataSource(@NotNull IDataProvider<Boolean> dataProvider) {
        UIEventListener tickableListener;
        if (dataProvider instanceof ITickable tickable) {
            tickableListener = e -> tickable.tick();
            addEventListener(UIEvents.TICK, tickableListener);
        } else {
            tickableListener = null;
        }
        var subscription = dataProvider.registerListener(this::setActive, true);
        if (tickableListener != null) {
            subscription.andThen(() -> removeEventListener(UIEvents.TICK, tickableListener));
        }
        this.dataSources.put(dataProvider, subscription);
        return this;
    }

    @Override
    public UIElement unbindDataSource(@NotNull IDataProvider<Boolean> dataProvider) {
        var removed = this.dataSources.remove(dataProvider);
        if (removed != null) {
            removed.unsubscribe();
        }
        return this;
    }

    @Override
    public Boolean getValue() {
        return this.isActive();
    }

    @Override
    public ClayWorkTableButton setValue(@Nullable Boolean value) {
        this.setActive(value != null && value);
        return this;
    }
}
