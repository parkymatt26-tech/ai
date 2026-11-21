package com.singlecheat.client.module.modules.movement;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.ModeSetting;

import java.util.List;

public class NoFallModule extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "NoFall mode", "Packet", List.of("Packet", "Spoof", "Zero")));

    public NoFallModule() {
        super("NoFall", "No fall damage", ModuleCategory.MOVEMENT);
    }

    @Override
    protected void onTick() {
        if (mc.player == null) return;
        mc.player.fallDistance = 0;
        // Actual packet spoofing is handled in ClientPlayerEntityMixin
    }

    public String getMode() { return mode.getValue(); }
}
