package net.kogepan.clayium.client.ldlib.elements;

import net.kogepan.clayium.client.ldlib.textures.ProgressArrowTexture;

import com.lowdragmc.lowdraglib2.gui.ui.elements.BindableUIElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProgressArrow extends BindableUIElement<Float> {

    private final ProgressArrowTexture texture = new ProgressArrowTexture();

    public ProgressArrow() {
        super();
        style(style -> style.backgroundTexture(texture));
        layout(layoutStyle -> layoutStyle.height(20));
    }

    @Override
    @NotNull
    public Float getValue() {
        return this.texture.getProgress();
    }

    @Override
    @NotNull
    public ProgressArrow setValue(@Nullable Float value, boolean notify) {
        this.texture.setProgress(value != null ? value : 0.0f);
        return this;
    }
}
