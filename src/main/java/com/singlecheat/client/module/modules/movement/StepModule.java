package com.singlecheat.client.module.modules.movement;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.NumberSetting;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;

public class StepModule extends Module {
    private static final double VANILLA_STEP = 0.6;

    private final NumberSetting height = register(new NumberSetting("Height", "Step height in blocks", 2.0, 0.5, 5.0, 0.5));

    public StepModule() {
        super("Step", "Step up blocks instantly", ModuleCategory.MOVEMENT);
    }

    @Override
    protected void onDisable() {
        setStepHeight(VANILLA_STEP);
    }

    @Override
    protected void onTick() {
        if (mc.player == null) return;
        setStepHeight(height.getValue());
    }

    private void setStepHeight(double value) {
        if (mc.player == null) return;
        EntityAttributeInstance attribute = mc.player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (attribute != null) {
            attribute.setBaseValue(value);
        }
    }
}
