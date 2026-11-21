package com.singlecheat.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.singlecheat.client.CheatClient;
import com.singlecheat.client.module.Module;
import com.singlecheat.client.setting.BooleanSetting;
import com.singlecheat.client.setting.ColorSetting;
import com.singlecheat.client.setting.ModeSetting;
import com.singlecheat.client.setting.NumberSetting;
import com.singlecheat.client.setting.Setting;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Persists module states, keybinds and settings to config/singlecheat.json.
 */
public class ConfigManager {
    private static final Path CONFIG_PATH = Paths.get("config", "singlecheat.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void load() {
        if (!Files.exists(CONFIG_PATH)) return;
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            JsonElement parsed = JsonParser.parseReader(reader);
            if (!parsed.isJsonObject()) return;
            JsonObject root = parsed.getAsJsonObject();
            JsonArray modules = root.getAsJsonArray("modules");
            if (modules == null) return;

            for (JsonElement element : modules) {
                if (!element.isJsonObject()) continue;
                JsonObject entry = element.getAsJsonObject();
                if (!entry.has("name")) continue;
                String name = entry.get("name").getAsString();

                CheatClient.moduleManager.getByName(name).ifPresent(module -> {
                    if (entry.has("keybind")) module.setKeybind(entry.get("keybind").getAsInt());
                    if (entry.has("enabled") && entry.get("enabled").getAsBoolean()) {
                        // setEnabled refuses in multiplayer, which is exactly what we want
                        module.setEnabled(true);
                    }
                    if (entry.has("settings") && entry.get("settings").isJsonObject()) {
                        JsonObject settings = entry.getAsJsonObject("settings");
                        for (Setting<?> setting : module.getSettings()) {
                            if (!settings.has(setting.getName())) continue;
                            apply(setting, settings.get(setting.getName()));
                        }
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("[SingleCheat] Failed to load config: " + e.getMessage());
        }
    }

    public void save() {
        if (CheatClient.moduleManager == null) return;
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            JsonObject root = new JsonObject();
            root.addProperty("version", CheatClient.VERSION);

            JsonArray modules = new JsonArray();
            for (Module module : CheatClient.moduleManager.getModules()) {
                JsonObject entry = new JsonObject();
                entry.addProperty("name", module.getName());
                entry.addProperty("enabled", module.isEnabled());
                entry.addProperty("keybind", module.getKeybind());

                JsonObject settings = new JsonObject();
                for (Setting<?> setting : module.getSettings()) {
                    write(settings, setting);
                }
                entry.add("settings", settings);
                modules.add(entry);
            }
            root.add("modules", modules);

            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                gson.toJson(root, writer);
            }
        } catch (Exception e) {
            System.err.println("[SingleCheat] Failed to save config: " + e.getMessage());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void apply(Setting setting, JsonElement value) {
        try {
            if (setting instanceof BooleanSetting) {
                setting.setValue(value.getAsBoolean());
            } else if (setting instanceof NumberSetting) {
                setting.setValue(value.getAsDouble());
            } else if (setting instanceof ModeSetting ms) {
                ms.setMode(value.getAsString());
            } else if (setting instanceof ColorSetting) {
                setting.setValue(value.getAsInt());
            }
        } catch (Exception e) {
            System.err.println("[SingleCheat] Bad value for setting " + setting.getName());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void write(JsonObject target, Setting setting) {
        Object value = setting.getValue();
        if (value instanceof Boolean bool) {
            target.addProperty(setting.getName(), bool);
        } else if (value instanceof Number number) {
            target.addProperty(setting.getName(), number);
        } else if (value instanceof String string) {
            target.addProperty(setting.getName(), string);
        }
    }
}
