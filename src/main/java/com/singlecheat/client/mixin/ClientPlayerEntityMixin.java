package com.singlecheat.client.mixin;

import com.singlecheat.client.CheatClient;
import com.singlecheat.client.module.modules.movement.NoFallModule;
import com.singlecheat.client.utils.SinglePlayerGuard;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NoFall: right before the client builds its movement packet we claim to be
 * standing on the ground and wipe the accumulated fall distance, so the
 * integrated server never registers a damaging fall.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Inject(method = "sendMovementPackets", at = @At("HEAD"), require = 0)
    private void singlecheat$noFall(CallbackInfo ci) {
        if (!SinglePlayerGuard.isSinglePlayer()) return;

        ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;
        if (self.fallDistance <= 2.0f) return;

        CheatClient.moduleManager.get(NoFallModule.class).ifPresent(noFall -> {
            if (!noFall.isEnabled()) return;
            self.setOnGround(true);
            self.fallDistance = 0.0f;
        });
    }
}
