package com.adryd.cauldronHijack.mixin.command;

import com.adryd.cauldron.impl.command.ClientCommandInternals;
import com.adryd.cauldronHijack.CommandInternals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientCommandInternals.class, remap = false)
public abstract class MixinCauldronClientCommandInternals {
    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;execute(Ljava/lang/String;Ljava/lang/Object;)I"))
    private static void cancel(String message, CallbackInfoReturnable<Boolean> cir) {
        if (message.length() > 1 && CommandInternals.fakeCommandNames.contains(message.substring(1).split(" ")[0])) {
            cir.setReturnValue(false);
        }
    }
}
