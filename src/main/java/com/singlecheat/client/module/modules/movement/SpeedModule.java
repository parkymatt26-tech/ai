package com.singlecheat.client.module.modules.movement;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.ModeSetting;
import com.singlecheat.client.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class SpeedModule extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Speed mode", "Vanilla", List.of("Vanilla", "Strafe", "Hop")));
    private final NumberSetting multiplier = register(new NumberSetting("Multiplier", "Speed multiplier", 2.0, 1.0, 10.0, 0.1));

    public SpeedModule() {
        super("Speed", "Move faster on ground", ModuleCategory.MOVEMENT);
    }

    @Override
    protected void onTick() {
        if (mc.player == null || mc.world == null) return;

        // Read the raw keys instead of the Input object (stable across mappings)
        float forward = 0f;
        float strafe = 0f;
        if (mc.options.forwardKey.isPressed()) forward += 1f;
        if (mc.options.backKey.isPressed()) forward -= 1f;
        if (mc.options.leftKey.isPressed()) strafe += 1f;
        if (mc.options.rightKey.isPressed()) strafe -= 1f;
        if (forward == 0f && strafe == 0f) return;

        String currentMode = mode.getValue();
        if (!mc.player.isOnGround() && currentMode.equals("Vanilla")) return;

        double baseSpeed = 0.215 * multiplier.getValue();
        double rad = Math.toRadians(mc.player.getYaw());

        // Forward is -Z when yaw == 0
        double forwardX = -Math.sin(rad);
        double forwardZ = Math.cos(rad);
        double rightX = Math.cos(rad);
        double rightZ = Math.sin(rad);

        double moveX = forwardX * forward + rightX * strafe;
        double moveZ = forwardZ * forward + rightZ * strafe;

        double len = Math.sqrt(moveX * moveX + moveZ * moveZ);
        if (len <= 0) return;
        moveX = moveX / len * baseSpeed;
        moveZ = moveZ / len * baseSpeed;

        if (currentMode.equals("Hop") && mc.player.isOnGround()) {
            mc.player.jump();
            mc.player.setVelocity(moveX, 0.42, moveZ);
        } else if (currentMode.equals("Strafe")) {
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(moveX, vel.y, moveZ);
        } else {
            mc.player.setVelocity(moveX, mc.player.getVelocity().y, moveZ);
        }
    }
}
