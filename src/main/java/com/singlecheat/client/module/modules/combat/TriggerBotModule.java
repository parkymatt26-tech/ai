package com.singlecheat.client.module.modules.combat;

import com.singlecheat.client.CheatClient;
import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;

public class TriggerBotModule extends Module {
    private final NumberSetting delay = register(new NumberSetting("Delay (ms)", "Delay after aiming", 100, 0, 500, 10));

    public TriggerBotModule() {
        super("TriggerBot", "Attacks when crosshair is on entity", ModuleCategory.COMBAT);
    }

    private long lastAttack = 0;

    @Override
    protected void onTick() {
        if (mc.player == null || mc.targetedEntity == null || mc.interactionManager == null) return;
        long now = System.currentTimeMillis();
        if (now - lastAttack < delay.getValue().longValue()) return;

        Entity target = mc.targetedEntity;
        if (!target.isAlive()) return;

        CheatClient.moduleManager.get(CriticalsModule.class).ifPresent(c -> c.beforeAttack(target));

        mc.player.attack(target);
        mc.player.swingHand(Hand.MAIN_HAND);
        lastAttack = now;
    }
}
