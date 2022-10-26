package com.adryd.cauldronHijack.mixin.command;

import com.mojang.brigadier.ParseResults;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.network.message.*;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerEntity.class)
public interface IMixinClientPlayerEntity {
	@Invoker("toDecoratedContents")
	DecoratedContents invokeToDecoratedContents(String message, @Nullable Text preview);

	@Invoker("createMessageMetadata")
	MessageMetadata invokeCreateMessageMetadata();

	@Invoker("signChatMessage")
	MessageSignatureData invokeSignChatMessage(MessageMetadata metadata, DecoratedContents content, LastSeenMessageList lastSeenMessages);

	@Invoker("signArguments")
	ArgumentSignatureDataMap invokeSignArguments(MessageMetadata messageMetadata, ParseResults<CommandSource> parseResults, Text preview, LastSeenMessageList lastSeen);
}
