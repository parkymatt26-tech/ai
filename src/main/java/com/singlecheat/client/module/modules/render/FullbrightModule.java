package com.singlecheat.client.module.modules.render;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.BooleanSetting;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullbrightModule extends Module {
    /**
     * Gamma is clamped to 0..1 by vanilla in 1.21.4, so the reliable way to get
     * full brightness is a locally-applied night vision effect, optionally
     * combined with maxed gamma for chunk lighting.
     */
    private final BooleanSetting nightVision = register(new BooleanSetting("Night Vision", "Use night vision effect", true));

    public FullbrightModule() {
        super("Fullbright", "Maximum brightness / night vision", ModuleCategory.RENDER);
    }

    @Override
    protected void onEnable() {
        if (mc.options != null) {
            mc.options.getGamma().setValue(1.0);
        }
    }

    @Override
    protected void onDisable() {
        if (mc.player != null) {
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }

    @Override
    protected void onTick() {
        if (mc.player == null) return;
        if (mc.options != null) {
            mc.options.getGamma().setValue(1.0);
        }
        if (!nightVision.getValue()) return;

        StatusEffectInstance current = mc.player.getStatusEffect(StatusEffects.NIGHT_VISION);
        if (current == null || current.getDuration() < 200) {
            // ambient = true keeps the screen tint subtle, no particles, no icon
            mc.player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.NIGHT_VISION, 600, 0, true, false, false));
        }
    }
}
