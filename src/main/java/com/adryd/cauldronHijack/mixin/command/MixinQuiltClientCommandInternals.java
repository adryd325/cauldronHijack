package com.adryd.cauldronHijack.mixin.command;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;
import org.quiltmc.qsl.command.impl.client.ClientCommandInternals;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static org.quiltmc.qsl.command.api.client.ClientCommandManager.DISPATCHER;


@Mixin(ClientCommandInternals.class)
public abstract class MixinQuiltClientCommandInternals {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    private static Text getErrorMessage(CommandSyntaxException e) {
        return null;
    }

    @Inject(method = "executeCommand", at = @At(value = "INVOKE", target = "Lorg/quiltmc/qsl/command/impl/client/ClientCommandInternals;shouldIgnore(Lcom/mojang/brigadier/exceptions/CommandExceptionType;)Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void alwaysLogException(String message, CallbackInfoReturnable<Boolean> cir, MinecraftClient client, QuiltClientCommandSource commandSource, CommandSyntaxException e) {
        boolean ignored = true;
        String commandName = message.substring(1).split(" ")[0];
        for (CommandNode<QuiltClientCommandSource> commandNode : DISPATCHER.getRoot().getChildren()) {
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

    /**
     * @author adryd <me@adryd.com>
     * @reason always marks the command as valid, therefore server commands can't overwrite client commands
     */
    @Overwrite
    public static <S extends CommandSource> boolean isCommandInvalidOrDummy(final ParseResults<S> parse) {
        return true;
    }
}
