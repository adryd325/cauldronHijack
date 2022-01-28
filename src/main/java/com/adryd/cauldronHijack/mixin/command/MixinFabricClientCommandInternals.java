package com.adryd.cauldronHijack.mixin.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.DISPATCHER;

@Mixin(value = ClientCommandInternals.class, remap = false)
public abstract class MixinFabricClientCommandInternals {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    private static Text getErrorMessage(CommandSyntaxException e) {
        return null;
    }

    @Inject(method = "executeCommand", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/command/client/ClientCommandInternals;isIgnoredException(Lcom/mojang/brigadier/exceptions/CommandExceptionType;)Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void alwaysLogException(String message, CallbackInfoReturnable<Boolean> cir, MinecraftClient client, FabricClientCommandSource commandSource, CommandSyntaxException e) {
        boolean ignored = true;
        String commandName = message.substring(1).split(" ")[0];
        for (CommandNode<FabricClientCommandSource> commandNode : DISPATCHER.getRoot().getChildren()) {
            if (commandNode.getName().equals(commandName)) {
                LOGGER.info("command name: {}", commandNode.getName());
                ignored = false;
                break;
            }
        }

        LOGGER.log(ignored ? Level.DEBUG : Level.WARN, "Syntax exception for client-sided command '{}'", message, e);

        if (ignored) {
            cir.setReturnValue(false);
            return;
        }

        commandSource.sendError(getErrorMessage(e));
        cir.setReturnValue(true);
    }
}
