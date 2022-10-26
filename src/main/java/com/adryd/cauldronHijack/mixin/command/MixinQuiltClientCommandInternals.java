package com.adryd.cauldronHijack.mixin.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;
import org.quiltmc.qsl.command.impl.client.ClientCommandInternals;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = ClientCommandInternals.class, remap = false)
public abstract class MixinQuiltClientCommandInternals {
	@Shadow
	@Final
	private static Logger LOGGER;

	@Shadow
	private static Text getErrorMessage(CommandSyntaxException e) {
		return null;
	}

	@Shadow
	@Final
	private static CommandDispatcher<QuiltClientCommandSource> DEFAULT_DISPATCHER;

	@Inject(method = "executeCommand", at = @At(value = "INVOKE", target = "Lorg/quiltmc/qsl/command/impl/client/ClientCommandInternals;shouldIgnore(Lcom/mojang/brigadier/exceptions/CommandExceptionType;)Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private static void alwaysLogException(String message, boolean ignorePrefix, CallbackInfoReturnable<Boolean> cir, MinecraftClient client, ClientCommandSource commandSource, CommandSyntaxException e) {
		boolean ignored = true;
		String commandName = message.substring(1).split(" ")[0];
		for (CommandNode<QuiltClientCommandSource> commandNode : DEFAULT_DISPATCHER.getRoot().getChildren()) {
			if (commandNode.getName().equals(commandName)) {
				ignored = false;
				break;
			}
		}

		if (!ignored) {
			LOGGER.warn("Syntax exception for client-sided command '{}'", message, e);
		} else {
			LOGGER.debug("Syntax exception for client-sided command '{}'", message, e);
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
