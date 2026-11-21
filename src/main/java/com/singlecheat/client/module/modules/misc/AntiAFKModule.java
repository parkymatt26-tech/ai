package com.singlecheat.client.module.modules.misc;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.NumberSetting;

public class AntiAFKModule extends Module {
    private final NumberSetting interval = register(new NumberSetting("Interval (s)", "Seconds between actions", 30.0, 5.0, 300.0, 5.0));

    private long lastAction = System.currentTimeMillis();

    public AntiAFKModule() {
        super("AntiAFK", "Prevents AFK kick (single-player safe)", ModuleCategory.MISC);
    }

    @Override
    protected void onTick() {
        if (mc.player == null) return;
        long now = System.currentTimeMillis();
        if (now - lastAction < interval.getValue().longValue() * 1000) return;

        // Small jump + look around
        mc.player.jump();
        mc.player.setYaw(mc.player.getYaw() + 180);
        lastAction = now;
    }
}
