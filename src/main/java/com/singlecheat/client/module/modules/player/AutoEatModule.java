package com.singlecheat.client.module.modules.player;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.NumberSetting;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.util.Hand;

public class AutoEatModule extends Module {
    private final NumberSetting hungerThreshold = register(new NumberSetting("Hunger Threshold", "Eat when hunger below", 14.0, 1.0, 20.0, 1.0));

    public AutoEatModule() {
        super("AutoEat", "Automatically eats food", ModuleCategory.PLAYER);
    }

    @Override
    protected void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;
        if (mc.player.getHungerManager().getFoodLevel() > hungerThreshold.getValue()) return;
        if (mc.player.isUsingItem()) return;

        int foodSlot = -1;
        for (int i = 0; i < 9; i++) {
            var stack = mc.player.getInventory().getStack(i);
            if (stack.contains(DataComponentTypes.FOOD)) {
                foodSlot = i;
                break;
            }
        }
        if (foodSlot == -1) return;

        int prev = mc.player.getInventory().selectedSlot;
        mc.player.getInventory().selectedSlot = foodSlot;
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        mc.player.getInventory().selectedSlot = prev;
    }
}
