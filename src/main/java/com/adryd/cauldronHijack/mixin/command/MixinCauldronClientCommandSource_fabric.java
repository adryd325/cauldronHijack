package com.adryd.cauldronHijack.mixin.command;

import com.adryd.cauldron.api.command.CauldronClientCommandSource;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CauldronClientCommandSource.class)
public abstract class MixinCauldronClientCommandSource_fabric implements FabricClientCommandSource {
}
