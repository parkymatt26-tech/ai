package com.singlecheat.client.module.modules.movement;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.BooleanSetting;
import com.singlecheat.client.setting.ModeSetting;
import com.singlecheat.client.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class FlyModule extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Fly mode", "Vanilla", List.of("Vanilla", "Creative", "Jetpack")));
    private final NumberSetting speed = register(new NumberSetting("Speed", "Horizontal speed multiplier", 1.0, 0.1, 5.0, 0.1));
    private final NumberSetting verticalSpeed = register(new NumberSetting("Vertical Speed", "Up/down speed", 1.0, 0.1, 5.0, 0.1));
    private final BooleanSetting noKick = register(new BooleanSetting("Anti-Kick", "Prevent fly kick (single-player safe)", true));

    public FlyModule() {
        super("Fly", "Fly anywhere - single player only", ModuleCategory.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        if (mc.player != null) {
            mc.player.getAbilities().allowFlying = true;
            mc.player.getAbilities().setFlySpeed((float) (0.05 * speed.getValue()));
        }
    }

    @Override
    protected void onDisable() {
        if (mc.player != null) {
            mc.player.getAbilities().allowFlying = false;
            mc.player.getAbilities().flying = false;
            mc.player.setVelocity(Vec3d.ZERO);
        }
    }

    @Override
    protected void onTick() {
        if (mc.player == null || mc.world == null) return;
        String m = mode.getValue();

        if (m.equals("Vanilla")) {
            mc.player.getAbilities().allowFlying = true;
            mc.player.getAbilities().flying = true;
            mc.player.getAbilities().setFlySpeed((float) (0.05 * speed.getValue()));
            mc.player.fallDistance = 0;
        } else if (m.equals("Creative")) {
            mc.player.getAbilities().allowFlying = true;
            mc.player.getAbilities().flying = true;
            mc.player.getAbilities().setFlySpeed((float) (0.1 * speed.getValue()));
            mc.player.fallDistance = 0;
        } else if (m.equals("Jetpack")) {
            // Hold jump to fly up, sneak to go down
            double up = 0;
            if (mc.options.jumpKey.isPressed()) up += 0.5 * verticalSpeed.getValue();
            if (mc.options.sneakKey.isPressed()) up -= 0.5 * verticalSpeed.getValue();
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(vel.x, up, vel.z);
            mc.player.fallDistance = 0;
            mc.player.setOnGround(true);
        }
    }
}
