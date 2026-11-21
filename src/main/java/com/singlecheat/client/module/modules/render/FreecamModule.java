package com.singlecheat.client.module.modules.render;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

public class FreecamModule extends Module {
    public static FreecamModule INSTANCE;

    private final NumberSetting speed = register(new NumberSetting("Speed", "Freecam speed", 1.0, 0.1, 5.0, 0.1));

    private Vec3d savedPos;
    private float savedYaw;
    private float savedPitch;

    public FreecamModule() {
        super("Freecam", "Fly camera detached from player", ModuleCategory.RENDER);
        INSTANCE = this;
    }

    @Override
    protected void onEnable() {
        if (mc.player != null) {
            savedPos = mc.player.getPos();
            savedYaw = mc.player.getYaw();
            savedPitch = mc.player.getPitch();
            mc.player.setNoGravity(true);
            mc.player.setVelocity(Vec3d.ZERO);
        }
    }

    @Override
    protected void onDisable() {
        if (mc.player != null && savedPos != null) {
            mc.player.setPos(savedPos);
            mc.player.setYaw(savedYaw);
            mc.player.setPitch(savedPitch);
            mc.player.setNoGravity(false);
        }
    }

    @Override
    protected void onTick() {
        if (mc.player == null) return;
        double s = 0.5 * speed.getValue();
        Vec3d vel = Vec3d.ZERO;

        float yaw = mc.player.getYaw();
        float pitch = mc.player.getPitch();
        double radYaw = Math.toRadians(yaw);
        double radPitch = Math.toRadians(pitch);

        // Forward vector
        double forwardX = -Math.sin(radYaw) * Math.cos(radPitch);
        double forwardY = -Math.sin(radPitch);
        double forwardZ = Math.cos(radYaw) * Math.cos(radPitch);

        if (mc.options.forwardKey.isPressed()) vel = vel.add(forwardX * s, forwardY * s, forwardZ * s);
        if (mc.options.backKey.isPressed()) vel = vel.add(-forwardX * s, -forwardY * s, -forwardZ * s);
        if (mc.options.leftKey.isPressed()) vel = vel.add(-Math.cos(radYaw) * s, 0, -Math.sin(radYaw) * s);
        if (mc.options.rightKey.isPressed()) vel = vel.add(Math.cos(radYaw) * s, 0, Math.sin(radYaw) * s);
        if (mc.options.jumpKey.isPressed()) vel = vel.add(0, s, 0);
        if (mc.options.sneakKey.isPressed()) vel = vel.add(0, -s, 0);

        mc.player.setVelocity(vel);
        mc.player.fallDistance = 0;
    }

    public boolean isActive() { return isEnabled(); }
}
