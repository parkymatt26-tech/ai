package com.singlecheat.client.module.modules.player;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.NumberSetting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class FastBreakModule extends Module {
    private final NumberSetting multiplier = register(new NumberSetting("Multiplier", "Break speed multiplier", 3.0, 1.0, 10.0, 0.5));

    public FastBreakModule() {
        super("FastBreak", "Break blocks faster", ModuleCategory.PLAYER);
    }

    @Override
    protected void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (!mc.options.attackKey.isPressed()) return;
        if (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.BLOCK) return;

        BlockHitResult hit = (BlockHitResult) mc.crosshairTarget;
        int extra = Math.max(0, Math.round(multiplier.getValue().floatValue()) - 1);
        for (int i = 0; i < extra; i++) {
            mc.interactionManager.updateBlockBreakingProgress(hit.getBlockPos(), hit.getSide());
        }
    }
}
