package com.adryd.cauldronHijack;

import com.google.gson.JsonObject;

public class KeybindInternals {
    private static final String key = "vanillaKeyBindings";
    public static JsonObject root;

    static {
        if (Jason.root.has(key)) {
            root = (JsonObject) Jason.root.get(key);
        } else {
            root = new JsonObject();
            Jason.root.add(key, root);
        }
    }
}
