package com.adryd.cauldronHijack.mixin.command;

import com.adryd.cauldronHijack.CommandInternals;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayNetworkHandler.class, priority = 1)
public abstract class MixinClientPlayNetworkHandler {

    @Shadow
    private CommandDispatcher<CommandSource> commandDispatcher;

    @Inject(method = "onCommandTree", at = @At("RETURN"))
    private void hijackCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci) {
        CommandInternals.realCommandDispatcher = commandDispatcher;
        commandDispatcher = CommandInternals.clientCommandDispatcher;
    }
}
