package com.adryd.cauldronHijack.mixin.command;

import com.adryd.cauldron.impl.command.ClientCommandInternals;
import com.adryd.cauldronHijack.CommandInternals;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientCommandInternals.class, remap = false)
public abstract class MixinCauldronClientCommandInternals {
	@Inject(method = "executeCommand", at = @At(value = "INVOKE", target = "Lcom/adryd/cauldron/impl/command/ClientCommandInternals;execute(Ljava/lang/String;Lcom/adryd/cauldron/api/command/CauldronClientCommandSource;)V"), cancellable = true)
	private static void cancel(String message, ClientPlayerEntity playerEntity, Text preview, CallbackInfoReturnable<Boolean> cir) {
		if (message.length() > 1 && CommandInternals.fakeCommandNames.contains(message.substring(1).split(" ")[0])) {
			cir.setReturnValue(false);
		}
	}
}
