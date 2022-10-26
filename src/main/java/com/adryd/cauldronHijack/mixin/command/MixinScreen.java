package com.adryd.cauldronHijack.mixin.command;

import com.adryd.cauldronHijack.CommandInternals;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Screen.class)
public abstract class MixinScreen {
    @Shadow
    protected abstract void insertText(String text, boolean override);

    // TODO: below, can we figure out if the text came from the server or client? if not we should check if the server has a command registered with the same name. if both client and server have the command, we need to do something
    // perhaps we could mixin to source.sendFeedback and look for clickable text recursively

    @Redirect(method = "handleTextClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;insertText(Ljava/lang/String;Z)V"))
    private void handleSuggestionInsert(Screen instance, String message, boolean override) {
        if (message.startsWith("/") && message.length() > 1 && CommandInternals.fakeCommandNames.contains(message.substring(1).split(" ")[0])) {
            this.insertText("." + message.substring(1), override);
            return;
        }
        this.insertText(message, override);
    }

    @Redirect(method = "handleTextClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendCommand(Ljava/lang/String;)Z"))
    private boolean handleSuggestionRun(ClientPlayerEntity instance, String message) {
        if (message.startsWith("/") && message.length() > 1 && CommandInternals.fakeCommandNames.contains(message.substring(1).split(" ")[0])) {
            instance.sendCommand("." + message.substring(1));
			return true;
        }
        instance.sendCommand(message);
		return true;
	}
}
