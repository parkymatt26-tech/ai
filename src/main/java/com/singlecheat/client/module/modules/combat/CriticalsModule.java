package com.singlecheat.client.module.modules.combat;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.ModeSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class CriticalsModule extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Criticals mode", "Jump", List.of("Jump", "MiniJump")));

    public CriticalsModule() {
        super("Criticals", "Always deal critical hits", ModuleCategory.COMBAT);
    }

    /** Called by KillAura / TriggerBot right before an attack. */
    public void beforeAttack(Entity target) {
        if (!isEnabled() || mc.player == null) return;
        if (!(target instanceof LivingEntity)) return;
        if (!mc.player.isOnGround()) return;

        if (mode.getValue().equals("Jump")) {
            mc.player.jump();
        } else {
            // Small upward kick: enough to set fallDistance > 0 for the crit check
            mc.player.setVelocity(mc.player.getVelocity().x, 0.11, mc.player.getVelocity().z);
        }
        // A tiny fall distance is what the crit check looks at
        mc.player.fallDistance = 0.1f;
    }

    @Override
    protected void onTick() {
        // Nothing to do while idle; crits are applied per-attack
    }
}
