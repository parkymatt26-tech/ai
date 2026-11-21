package com.singlecheat.client.mixin;

import com.singlecheat.client.CheatClient;
import com.singlecheat.client.module.modules.render.XRayModule;
import com.singlecheat.client.utils.SinglePlayerGuard;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * XRay part 2: hidden blocks must not cull their neighbours, otherwise the ore
 * faces next to stone would be skipped. Also marks hidden blocks non-opaque.
 */
@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class BlockStateMixin {

    @Inject(method = "isOpaqueFullCube", at = @At("HEAD"), cancellable = true, require = 0)
    private void singlecheat$xrayOpacity(CallbackInfoReturnable<Boolean> cir) {
        if (!SinglePlayerGuard.isSinglePlayer()) return;

        XRayModule xray = CheatClient.moduleManager.get(XRayModule.class).orElse(null);
        if (xray == null || !xray.isEnabled()) return;

        BlockState self = (BlockState) (Object) this;
        if (xray.shouldHide(self)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isOpaque", at = @At("HEAD"), cancellable = true, require = 0)
    private void singlecheat$xrayOpaque(CallbackInfoReturnable<Boolean> cir) {
        if (!SinglePlayerGuard.isSinglePlayer()) return;

        XRayModule xray = CheatClient.moduleManager.get(XRayModule.class).orElse(null);
        if (xray == null || !xray.isEnabled()) return;

        BlockState self = (BlockState) (Object) this;
        if (xray.shouldHide(self)) {
            cir.setReturnValue(false);
        }
    }
}
