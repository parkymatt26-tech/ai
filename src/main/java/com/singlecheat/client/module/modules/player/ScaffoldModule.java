package com.singlecheat.client.module.modules.player;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.BooleanSetting;
import com.singlecheat.client.setting.ModeSetting;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public class ScaffoldModule extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Scaffold mode", "Normal", List.of("Normal", "Tower", "GodBridge")));
    private final BooleanSetting autoSwitch = register(new BooleanSetting("Auto Switch", "Auto-select blocks", true));
    private final BooleanSetting eagle = register(new BooleanSetting("Eagle", "Sneak at edge (safety)", false));

    public ScaffoldModule() {
        super("Scaffold", "Automatically places blocks under you", ModuleCategory.PLAYER);
    }

    @Override
    protected void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        BlockPos below = mc.player.getBlockPos().down();

        // Tower mode: place below while jumping
        if (mode.getValue().equals("Tower") && mc.options.jumpKey.isPressed()) {
            // handled below same as normal
        }

        if (!mc.world.getBlockState(below).isAir() && !mc.world.getBlockState(below).isReplaceable()) return;

        // Find block in hotbar
        int blockSlot = -1;
        for (int i = 0; i < 9; i++) {
            var stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem bi) {
                Block b = bi.getBlock();
                if (!b.getDefaultState().isAir()) {
                    blockSlot = i;
                    break;
                }
            }
        }
        if (blockSlot == -1) return;

        int prevSlot = mc.player.getInventory().selectedSlot;
        if (autoSwitch.getValue()) mc.player.getInventory().selectedSlot = blockSlot;

        // Try to place against adjacent solid block
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = below.add(dir.getVector());
            if (!mc.world.getBlockState(neighbor).isAir()) {
                BlockHitResult hit = new BlockHitResult(
                        mc.player.getPos().add(0.5, 0.5, 0.5),
                        dir.getOpposite(),
                        neighbor,
                        false
                );
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                mc.player.swingHand(Hand.MAIN_HAND);
                break;
            }
        }

        if (autoSwitch.getValue()) mc.player.getInventory().selectedSlot = prevSlot;
    }
}
