package com.singlecheat.client.module.modules.combat;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.NumberSetting;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotemModule extends Module {
    private final NumberSetting healthThreshold = register(new NumberSetting("Health Threshold", "Move totem when HP below this", 10.0, 1.0, 20.0, 0.5));

    public AutoTotemModule() {
        super("AutoTotem", "Automatically moves Totem to offhand", ModuleCategory.COMBAT);
    }

    @Override
    protected void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;
        if (mc.player.getHealth() > healthThreshold.getValue()) return;

        // Check offhand already has totem
        if (mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) return;

        // Find totem in inventory
        int totemSlot = -1;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            if (mc.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                totemSlot = i;
                break;
            }
        }
        if (totemSlot == -1) return;

        // Swap to offhand (slot 40 in player screen handler)
        // Player inventory slots 0-8 are hotbar, 9-35 main, 36-44 armor/offhand in screen handler
        // Use clickSlot with SWAP action
        int screenSlot = totemSlot < 9 ? totemSlot + 36 : totemSlot;
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, screenSlot, 40, SlotActionType.SWAP, mc.player);
    }
}
