package com.adryd.cauldronHijack;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class CauldronHijackClientMod implements ClientModInitializer, net.fabricmc.api.ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		onInitializeClient();
	}

	@Override
	public void onInitializeClient() {
		Jason.read();
	}
}
