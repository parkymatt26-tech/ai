package com.singlecheat.client.module.modules.movement;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.BooleanSetting;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class JesusModule extends Module {
    private final BooleanSetting sink = register(new BooleanSetting("Sink", "Hold sneak to sink", true));

    public JesusModule() {
        super("Jesus", "Walk on water", ModuleCategory.MOVEMENT);
    }

    @Override
    protected void onTick() {
        if (mc.player == null || mc.world == null) return;

        BlockPos below = mc.player.getBlockPos().down();
        boolean waterBelow = mc.world.getBlockState(below).isOf(Blocks.WATER);
        boolean inWater = mc.player.isTouchingWater();

        if (!waterBelow && !inWater) return;
        if (sink.getValue() && mc.options.sneakKey.isPressed()) return;

        var velocity = mc.player.getVelocity();
        // Stop the vertical sink while keeping horizontal momentum
        mc.player.setVelocity(velocity.x, Math.max(velocity.y, 0.0), velocity.z);
        mc.player.setOnGround(true);
        mc.player.fallDistance = 0;
    }
}
