package com.adryd.cauldronHijack.mixin.command;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayerEntity.class, priority = 1)
public abstract class MixinClientPlayerEntity {
    @Inject(method = "sendChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci) {
        // Hack so we dont have to mixin to Keyboard and GameModeSelectionScreen
        if (!message.startsWith("/gamemode ")) {
            ci.cancel();
        }
    }
}
