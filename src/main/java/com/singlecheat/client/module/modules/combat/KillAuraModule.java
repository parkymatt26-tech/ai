package com.singlecheat.client.module.modules.combat;

import com.singlecheat.client.CheatClient;
import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.BooleanSetting;
import com.singlecheat.client.setting.ModeSetting;
import com.singlecheat.client.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.Hand;

import java.util.List;

public class KillAuraModule extends Module {
    private final NumberSetting range = register(new NumberSetting("Range", "Attack range in blocks", 4.5, 1.0, 6.0, 0.1));
    private final NumberSetting delay = register(new NumberSetting("Delay (ms)", "Delay between attacks", 250, 0, 1000, 10));
    private final ModeSetting targetMode = register(new ModeSetting("Targets", "Who to attack", "All", List.of("All", "Hostile", "Passive", "Players")));
    private final BooleanSetting autoBlock = register(new BooleanSetting("Auto Block", "Block while attacking (shield)", false));
    private final BooleanSetting rotate = register(new BooleanSetting("Rotate", "Rotate to target", true));

    private long lastAttack = 0;

    public KillAuraModule() {
        super("KillAura", "Automatically attacks nearby entities", ModuleCategory.COMBAT);
    }

    @Override
    protected void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        long now = System.currentTimeMillis();
        if (now - lastAttack < delay.getValue().longValue()) return;

        Entity target = findTarget();
        if (target == null) return;

        if (rotate.getValue()) {
            // Face target
            double dx = target.getX() - mc.player.getX();
            double dy = target.getY() + target.getHeight() / 2 - mc.player.getEyePos().y;
            double dz = target.getZ() - mc.player.getZ();
            double horizontal = Math.sqrt(dx * dx + dz * dz);
            float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
            float pitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));
            mc.player.setYaw(yaw);
            mc.player.setPitch(pitch);
        }

        CheatClient.moduleManager.get(CriticalsModule.class).ifPresent(c -> c.beforeAttack(target));

        mc.player.attack(target);
        mc.player.swingHand(Hand.MAIN_HAND);
        lastAttack = now;
    }

    private Entity findTarget() {
        Entity best = null;
        double bestDist = range.getValue();
        var searchBox = mc.player.getBoundingBox().expand(bestDist);
        for (Entity e : mc.world.getEntitiesByClass(Entity.class, searchBox, e -> true)) {
            if (e == mc.player || !e.isAlive() || e.isRemoved()) continue;
            if (e instanceof LivingEntity le && le.getHealth() <= 0) continue;

            String mode = targetMode.getValue();
            if (mode.equals("Hostile") && !(e instanceof HostileEntity)) continue;
            if (mode.equals("Passive") && !(e instanceof PassiveEntity)) continue;
            if (mode.equals("Players") && !(e instanceof net.minecraft.entity.player.PlayerEntity)) continue;

            double dist = mc.player.distanceTo(e);
            if (dist <= bestDist) {
                bestDist = dist;
                best = e;
            }
        }
        return best;
    }
}
