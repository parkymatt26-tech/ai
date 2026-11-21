package com.singlecheat.client.module.modules.movement;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.NumberSetting;

/**
 * Speeds up world simulation by running extra entity ticks per client tick.
 * Single-player only (the integrated server shares the process, so the extra
 * ticks are processed immediately).
 */
public class TimerModule extends Module {
    private final NumberSetting speed = register(new NumberSetting("Speed", "Game speed multiplier (1.0 = normal)", 1.5, 0.1, 5.0, 0.1));

    public TimerModule() {
        super("Timer", "Speed up / slow down world simulation", ModuleCategory.MOVEMENT);
    }

    @Override
    protected void onTick() {
        if (mc.world == null) return;
        int extra = Math.round(getSpeed() - 1.0f);
        for (int i = 0; i < extra; i++) {
            mc.world.tickEntities();
        }
    }

    public float getSpeed() {
        return speed.getValue().floatValue();
    }
}
