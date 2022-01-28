package com.adryd.cauldronHijack;

import net.fabricmc.api.ClientModInitializer;

public class CauldronHijackClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Jason.read();
    }
}
