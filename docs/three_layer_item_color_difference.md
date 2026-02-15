# 3-Layer Tinted Items: Original vs Modern Color Difference

## Summary

Possible reasons why 3-layer tinted items (ingots, plates) look different between ClayiumOriginal (1.7.10) and ClayiumModern (1.21.1):

---

## 1. Texture content (most likely)

**Original:** Uses `ingot_base`, `ingot_dark`, `ingot_light` (and `plate_*` / `largeplate_*`) from `GenericMaterialIcon`. Each pass is drawn with `glColor4f(R,G,B,1)` and the texture, so the result is **vertex color × texture** (per component).

**Modern:** Uses the same layer structure and passes tint from `ItemColors` (R, G, B, A) into the vertex buffer; the shader effectively does **tint × texture** per layer.

If the **texture files** in Modern are not the same as in Original (different brightness, alpha, or design), the same RGB tint will produce a different final color.

- **Ingots:** The plan assumed ingot textures already existed in Modern and did **not** copy them from Original. So `ingot_base/dark/light` in Modern may have been created elsewhere and can differ from Original.
- **Plates:** We copied `plate_*` and `largeplate_*` from Original, so they are more likely to match; any remaining difference is more likely due to rendering or color space.

**Action:** Copy `ingot_base.png`, `ingot_dark.png`, `ingot_light.png` from ClayiumOriginal into Modern’s `assets/clayium/textures/item/` and compare again.

---

## 2. Color format and pipeline

**Original (ItemDamagedRenderer):**

- `getColorFromItemStack(stack, pass)` returns `rgb2int(r,g,b) = (r<<16)+(g<<8)+b` (no alpha).
- Renderer uses `(k >> 16 & 255) / 255.0F` for R, same for G and B, and sets `glColor4f(r, g, b, 1.0F)`.
- So the effective color is **(R, G, B)** in 0–255, used as a vertex color multiplier.

**Modern (ItemRenderer + ItemColors):**

- We return `0xFFRRGGBB` from `ColoredIngotTints` / `ColoredPlateTints`.
- Code uses `FastColor.ARGB32.red(i)`, `.green(i)`, `.blue(i)`, `.alpha(i)` and passes `(r/255, g/255, b/255, a/255)` to `putBulkData`.
- So we are also passing **(R, G, B, A)** as a vertex color; the pipeline uses it to tint the quad.

So both sides use the same conceptual idea (vertex color × texture). The **numeric** color values are compatible; the main difference is likely texture or color space, not the format of the tint.

---

## 3. sRGB vs linear color space

In 1.21.1, rendering is often done in sRGB. The engine may:

- Treat texture samples in sRGB and convert to linear for shading.
- Use the tint as sRGB or linear depending on the pipeline.

In 1.7.10, the fixed pipeline usually worked in a linear-ish space. So the **same (R, G, B)** can look darker or lighter in Modern if the pipeline applies sRGB/linear conversions.

**Action:** If colors are systematically too dark or too bright, try converting the tint from linear to sRGB (or the reverse) when registering in `ColoredIngotTints` / `ColoredPlateTints`. This is a second step after aligning textures.

---

## 4. Layer blending and alpha

**Original:** Draws 3 passes in order; each pass uses one texture and one color. The final look depends on OpenGL blend mode and each texture’s **alpha**. So the alpha of `ingot_base`, `ingot_dark`, and `ingot_light` strongly affects how the three layers combine.

**Modern:** `ItemModelGenerator` assigns `tintIndex` 0, 1, 2 to layer0, layer1, layer2. Each quad is drawn with its tint; layer order and texture alpha again define the result.

If the **alpha** (or luminance) of the layer textures in Modern differs from Original, the same tint will produce a different composite color even with the same RGB values.

---

## Recommended order of checks

1. **Replace ingot (and if needed plate) textures** in Modern with the exact files from Original, then compare.
2. If the difference remains, **inspect one material** (e.g. Silicon Ingot) and try a small **sRGB/linear** correction on the tint to see if the result gets closer to Original.
3. If still off, compare **alpha and luminance** of each layer texture (base/dark/light) between Original and Modern and align them if they differ.
