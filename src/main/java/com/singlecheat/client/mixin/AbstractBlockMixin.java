package com.singlecheat.client.mixin;

import com.singlecheat.client.CheatClient;
import com.singlecheat.client.module.modules.render.XRayModule;
import com.singlecheat.client.utils.SinglePlayerGuard;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockRenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * XRay part 1: blocks that should be hidden are reported as INVISIBLE, so the
 * chunk mesher never writes their quads. Ores keep their normal model.
 */
@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true, require = 0)
    private void singlecheat$xrayRenderType(BlockState state, CallbackInfoReturnable<BlockRenderType> cir) {
        if (!SinglePlayerGuard.isSinglePlayer()) return;

        XRayModule xray = CheatClient.moduleManager.get(XRayModule.class).orElse(null);
        if (xray == null || !xray.isEnabled()) return;

        if (xray.shouldHide(state)) {
            cir.setReturnValue(BlockRenderType.INVISIBLE);
        }
    }
}
