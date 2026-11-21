package com.singlecheat.client.module.modules.movement;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.BooleanSetting;

public class SprintModule extends Module {
    private final BooleanSetting omni = register(new BooleanSetting("Omni-Sprint", "Sprint in all directions", true));
    private final BooleanSetting keepSprint = register(new BooleanSetting("Keep Sprint", "Keep sprinting when hit", true));

    public SprintModule() {
        super("Sprint", "Always sprint", ModuleCategory.MOVEMENT);
    }

    @Override
    protected void onTick() {
        if (mc.player == null) return;
        mc.player.setSprinting(true);
        if (keepSprint.getValue()) {
            mc.player.setSprinting(true);
        }
    }
}
