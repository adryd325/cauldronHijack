package com.adryd.cauldronHijack;

import com.google.gson.*;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

// This is my friend
// His name is Jason
public class Jason {
    private static final Logger LOGGER = LoggerFactory.getLogger(Jason.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_PATH = new File(MinecraftClient.getInstance().runDirectory, "config");
    private static final File CONFIG_FILE = new File(CONFIG_PATH, "cauldron-hijack.json");
    @Nullable
    public static JsonObject root;

    public static void read() {
        if (Objects.nonNull(root)) return;
        if (CONFIG_FILE.exists() && CONFIG_FILE.isFile() && CONFIG_FILE.canRead()) {
            String fileName = CONFIG_FILE.getAbsolutePath();
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {
                JsonElement element = JsonParser.parseReader(reader);
                if (element != null && element.isJsonObject()) {
                    root = (JsonObject) element;
                }
            } catch (Exception e) {
                LOGGER.error("Failed to parse JSON file \"{}\"", fileName, e);
            }
        } else {
            root = new JsonObject();
        }
    }

    public static void write() {
        if (Objects.isNull(root)) return;
        File tmpFile = new File(CONFIG_PATH, CONFIG_FILE.getName() + ".tmp");

        if (tmpFile.exists()) {
            tmpFile = new File(CONFIG_PATH, CONFIG_FILE.getName() + "." + UUID.randomUUID() + ".tmp");
        }

        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tmpFile), StandardCharsets.UTF_8);
            writer.write(GSON.toJson(root));
            writer.close();

            if (CONFIG_FILE.exists() && CONFIG_FILE.isFile() && !CONFIG_FILE.delete()) {
                LOGGER.error("Failed to delete file \"{}\"", CONFIG_FILE.getAbsolutePath());
            } else {
                tmpFile.renameTo(CONFIG_FILE);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to write JSON to file \"{}\"", tmpFile.getAbsolutePath(), e);
        }
    }
}
