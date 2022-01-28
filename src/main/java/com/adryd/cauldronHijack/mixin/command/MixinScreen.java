package com.adryd.cauldronHijack.mixin.command;

import com.adryd.cauldron.api.command.ClientCommandManager;
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

    @Redirect(method = "sendMessage(Ljava/lang/String;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendChatMessage(Ljava/lang/String;)V"))
    private void sendMessage(ClientPlayerEntity instance, String message) {
        if (message.startsWith(ClientCommandManager.COMMAND_PREFIX + "")) {
            if (message.startsWith("./")) {
                CommandInternals.sendRealMessage(message);
                return;
            }
            boolean didSend = false;
            for (String fakeCommand : CommandInternals.fakeCommandNames) {
                if ((message.startsWith(ClientCommandManager.COMMAND_PREFIX + fakeCommand + " ") ||
                        message.equals(ClientCommandManager.COMMAND_PREFIX + fakeCommand))) {
                    message = "/" + message.substring(1);
                    instance.sendChatMessage(message);
                    didSend = true;
                    break;
                }
            }
            if (!didSend) {
                instance.sendChatMessage(message);
            }
        } else {
            CommandInternals.sendRealMessage(message);
        }
    }

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

    @Redirect(method = "handleTextClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;sendMessage(Ljava/lang/String;Z)V"))
    private void handleSuggestionRun(Screen instance, String message, boolean toHud) {
        if (message.startsWith("/") && message.length() > 1 && CommandInternals.fakeCommandNames.contains(message.substring(1).split(" ")[0])) {
            instance.sendMessage("." + message.substring(1), toHud);
            return;
        }
        instance.sendMessage(message, toHud);
    }
}
