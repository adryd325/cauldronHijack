package com.adryd.cauldronHijack.mixin.command;

import com.adryd.cauldronHijack.CommandInternals;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandSuggestor.class)
public abstract class MixinCommandSuggestor {
    @Redirect(method = "refresh", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;getCommandDispatcher()Lcom/mojang/brigadier/CommandDispatcher;"))
    private CommandDispatcher<CommandSource> getCommandDispatcherRefresh(ClientPlayNetworkHandler instance) {
        return CommandInternals.realCommandDispatcher;
    }

    @Redirect(method = "showUsages", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;getCommandDispatcher()Lcom/mojang/brigadier/CommandDispatcher;"))
    private CommandDispatcher<CommandSource> getCommandDispatcherShowUsages(ClientPlayNetworkHandler instance) {
        return CommandInternals.realCommandDispatcher;
    }

    @Inject(method = "refresh", at = @At("HEAD"))
    private void merge(CallbackInfo ci) {
        CommandInternals.merge();
    }
}
