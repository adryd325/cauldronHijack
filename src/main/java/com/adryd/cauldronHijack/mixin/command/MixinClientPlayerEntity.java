package com.adryd.cauldronHijack.mixin.command;

import com.adryd.cauldron.api.command.ClientCommandManager;
import com.adryd.cauldron.impl.command.ClientCommandInternals;
import com.adryd.cauldronHijack.CommandInternals;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayerEntity.class, priority = 1)
public abstract class MixinClientPlayerEntity {
	@Inject(method = "sendCommandInternal", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;createMessageMetadata()Lnet/minecraft/network/message/MessageMetadata;"), cancellable = true)
	private void onSendChatMessage(String message, Text preview, CallbackInfo ci) {
		ClientCommandInternals.executeCommand(ClientCommandManager.COMMAND_PREFIX + message, (ClientPlayerEntity) (Object) this, preview);
		ci.cancel();
	}
}
