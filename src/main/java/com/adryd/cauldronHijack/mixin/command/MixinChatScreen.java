package com.adryd.cauldronHijack.mixin.command;

import com.adryd.cauldron.api.command.ClientCommandManager;
import com.adryd.cauldron.impl.command.ClientCommandInternals;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.adryd.cauldronHijack.CommandInternals;

@Mixin(ChatScreen.class)
public class MixinChatScreen {
	@Redirect(method="sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendCommand(Ljava/lang/String;Lnet/minecraft/text/Text;)V"))
	private void sendCommand(ClientPlayerEntity instance, String message, Text preview) {
		CommandInternals.sendRealCommand(instance, message, preview);
	}

	@Redirect(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V"))
	private void sendMessage(ClientPlayerEntity instance, String message, Text preview) {
		if (message.startsWith(ClientCommandManager.COMMAND_PREFIX + "")) {
			if (message.startsWith(ClientCommandManager.COMMAND_PREFIX + "/")) {
				CommandInternals.sendRealMessage(instance, message, preview);
				return;
			}
			instance.sendCommand(message.substring(1), preview);
		} else {
			CommandInternals.sendRealMessage(instance, message, preview);
		}
	}
}
