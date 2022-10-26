package com.adryd.cauldronHijack;

import com.adryd.cauldron.api.command.CauldronClientCommandSource;
import com.adryd.cauldron.api.command.ClientCommandManager;
import com.adryd.cauldronHijack.mixin.command.IMixinClientPlayerEntity;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.message.*;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adryd.cauldron.api.command.ClientCommandManager.DISPATCHER;

public class CommandInternals {
	public static final CommandDispatcher<CommandSource> clientCommandDispatcher = new CommandDispatcher<>();
	public static final List<String> fakeCommandNames = new ArrayList<>();
	private static final MinecraftClient client = MinecraftClient.getInstance();
	public static CommandDispatcher<CommandSource> realCommandDispatcher;

	public static void sendRealMessage(ClientPlayerEntity instance, String message, Text preview) {
		DecoratedContents decoratedContents =  ((IMixinClientPlayerEntity) instance).invokeToDecoratedContents(message, preview);
		MessageMetadata messageMetadata = ((IMixinClientPlayerEntity) instance).invokeCreateMessageMetadata();
		LastSeenMessageList.Acknowledgment acknowledgment = instance.networkHandler.consumeAcknowledgment();
		MessageSignatureData messageSignatureData = ((IMixinClientPlayerEntity) instance).invokeSignChatMessage(messageMetadata, decoratedContents, acknowledgment.lastSeen());
		instance.networkHandler.sendPacket(new ChatMessageC2SPacket(decoratedContents.plain(), messageMetadata.timestamp(), messageMetadata.salt(), messageSignatureData, decoratedContents.isDecorated(), acknowledgment));

	}

	public static void sendRealCommand(ClientPlayerEntity instance, String command, Text preview) {
		ParseResults<CommandSource> parseResults = instance.networkHandler.getCommandDispatcher().parse(command, instance.networkHandler.getCommandSource());
		MessageMetadata messageMetadata = ((IMixinClientPlayerEntity) instance).invokeCreateMessageMetadata();
		LastSeenMessageList.Acknowledgment acknowledgment = instance.networkHandler.consumeAcknowledgment();
		ArgumentSignatureDataMap argumentSignatureDataMap = ((IMixinClientPlayerEntity) instance).invokeSignArguments(messageMetadata, parseResults, preview, acknowledgment.lastSeen());
		instance.networkHandler.sendPacket(new CommandExecutionC2SPacket(command, messageMetadata.timestamp(), messageMetadata.salt(), argumentSignatureDataMap, preview != null, acknowledgment));
	}

	public static void merge() {
		HashMap<CommandNode<CommandSource>, CommandNode<CauldronClientCommandSource>> map = Maps.newHashMap();
		map.put(clientCommandDispatcher.getRoot(), DISPATCHER.getRoot());
		createSuggestionsTree(clientCommandDispatcher.getRoot(), ClientCommandManager.DISPATCHER.getRoot(), client.getNetworkHandler().getCommandSource(), map, 0);
	}

	private static void createSuggestionsTree(CommandNode<CommandSource> tree, CommandNode<CauldronClientCommandSource> result, ClientCommandSource source, Map<CommandNode<CommandSource>, CommandNode<CauldronClientCommandSource>> resultNodes, int level) {
		for (CommandNode<CommandSource> commandNode : tree.getChildren()) {
			if (commandNode.canUse(source)) {
				@SuppressWarnings({"unchecked", "rawtypes"}) ArgumentBuilder<CauldronClientCommandSource, ?> argumentBuilder = (ArgumentBuilder) commandNode.createBuilder();
				argumentBuilder.executes((context) -> 0);
				if (argumentBuilder.getCommand() != null) {
					argumentBuilder.executes((context) -> 0);
				}

				if (argumentBuilder instanceof RequiredArgumentBuilder) {
					@SuppressWarnings({"unchecked", "rawtypes"}) RequiredArgumentBuilder<CommandSource, ?> requiredArgumentBuilder = (RequiredArgumentBuilder) argumentBuilder;
					if (requiredArgumentBuilder.getSuggestionsProvider() != null) {
						requiredArgumentBuilder.suggests(SuggestionProviders.getLocalProvider(requiredArgumentBuilder.getSuggestionsProvider()));
					}
				}

				if (argumentBuilder.getRedirect() != null) {
					argumentBuilder.redirect(resultNodes.get(argumentBuilder.getRedirect()));
				}


				CommandNode<CauldronClientCommandSource> requiredArgumentBuilder = argumentBuilder.build();

				if (fakeCommandNames.contains(requiredArgumentBuilder.getName()) || requiredArgumentBuilder.getName().equals("help")) {
					// Don't register multiple times and don't let anything touch our help command
					continue;
				}

				resultNodes.put(commandNode, requiredArgumentBuilder);
				result.addChild(requiredArgumentBuilder);

				if (level == 0) {
					fakeCommandNames.add(requiredArgumentBuilder.getName());
				}

				if (!commandNode.getChildren().isEmpty()) {
					createSuggestionsTree(commandNode, requiredArgumentBuilder, source, resultNodes, level + 1);
				}
			}
		}
	}
}
