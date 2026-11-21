package com.singlecheat.client.mixin;

import com.singlecheat.client.CheatClient;
import com.singlecheat.client.module.modules.render.NoHurtCamModule;
import com.singlecheat.client.utils.SinglePlayerGuard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NoHurtCam: suppress the hurt camera tilt/shake.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(method = "bobView", at = @At("HEAD"), require = 0)
    private void singlecheat$noHurtCam(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (!SinglePlayerGuard.isSinglePlayer()) return;

        CheatClient.moduleManager.get(NoHurtCamModule.class).ifPresent(module -> {
            if (!module.isEnabled()) return;
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.hurtTime = 0;
            }
        });
    }
}
