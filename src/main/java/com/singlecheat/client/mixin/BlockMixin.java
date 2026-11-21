package com.singlecheat.client.mixin;

import com.singlecheat.client.CheatClient;
import com.singlecheat.client.module.modules.render.XRayModule;
import com.singlecheat.client.utils.SinglePlayerGuard;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * XRay part 3: when a neighbour is being hidden, its surrounding faces must
 * still be drawn or the ore blocks would be surrounded by holes.
 */
@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true, require = 0)
    private static void singlecheat$xrayDrawSide(BlockState state, BlockState otherState,
                                                 Direction side, CallbackInfoReturnable<Boolean> cir) {
        if (!SinglePlayerGuard.isSinglePlayer()) return;

        XRayModule xray = CheatClient.moduleManager.get(XRayModule.class).orElse(null);
        if (xray == null || !xray.isEnabled()) return;

        if (xray.shouldHide(otherState)) {
            cir.setReturnValue(true);
        }
    }
}
