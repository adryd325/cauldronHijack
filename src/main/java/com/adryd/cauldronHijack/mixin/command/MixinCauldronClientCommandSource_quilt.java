package com.adryd.cauldronHijack.mixin.command;

import com.adryd.cauldron.api.command.CauldronClientCommandSource;
import net.minecraft.client.MinecraftClient;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CauldronClientCommandSource.class)
public abstract class MixinCauldronClientCommandSource_quilt implements QuiltClientCommandSource {
    @Shadow @Final private MinecraftClient client;

    @Override
    public Object getMeta(String key) {
        return ((QuiltClientCommandSource) client.getNetworkHandler().getCommandSource()).getMeta(key);
    }

    @Override
    public void setMeta(String key, Object value) {
        ((QuiltClientCommandSource) client.getNetworkHandler().getCommandSource()).setMeta(key, value);
    }
}
