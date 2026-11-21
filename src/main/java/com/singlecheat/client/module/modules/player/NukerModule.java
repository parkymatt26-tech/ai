package com.singlecheat.client.module.modules.player;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.NumberSetting;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class NukerModule extends Module {
    private final NumberSetting range = register(new NumberSetting("Range", "Break range", 4.5, 1.0, 6.0, 0.1));
    private final NumberSetting delay = register(new NumberSetting("Delay (ms)", "Break delay", 100, 0, 1000, 10));

    private long lastBreak = 0;

    public NukerModule() {
        super("Nuker", "Breaks blocks around you automatically", ModuleCategory.PLAYER);
    }

    @Override
    protected void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        long now = System.currentTimeMillis();
        if (now - lastBreak < delay.getValue().longValue()) return;

        BlockPos playerPos = mc.player.getBlockPos();
        int r = range.getValue().intValue();

        outer:
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    if (state.isAir()) continue;
                    if (state.getHardness(mc.world, pos) < 0) continue; // unbreakable (bedrock etc)
                    if (mc.player.getEyePos().distanceTo(net.minecraft.util.math.Vec3d.ofCenter(pos)) > range.getValue()) continue;

                    mc.interactionManager.updateBlockBreakingProgress(pos, Direction.UP);
                    lastBreak = now;
                    break outer;
                }
            }
        }
    }

}
