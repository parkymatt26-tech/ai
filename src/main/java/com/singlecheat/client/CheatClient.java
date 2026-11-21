package com.singlecheat.client;

import com.singlecheat.client.command.CommandManager;
import com.singlecheat.client.config.ConfigManager;
import com.singlecheat.client.gui.ClickGuiScreen;
import com.singlecheat.client.gui.HudRenderer;
import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleManager;
import com.singlecheat.client.utils.ESPRenderer;
import com.singlecheat.client.utils.SinglePlayerGuard;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

/**
 * SingleCheat Client - SINGLE PLAYER ONLY.
 *
 * <p>Every module is hard-locked to single-player / the integrated server.
 * The moment a remote multiplayer connection is detected all modules are
 * disabled and the tick loop stops feeding them, so nothing can re-enable.
 */
public class CheatClient implements ClientModInitializer {
    public static final String MOD_ID = "singlecheat";
    public static final String MOD_NAME = "SingleCheat";
    public static final String VERSION = "1.0.0";

    public static CheatClient INSTANCE;
    public static ModuleManager moduleManager;
    public static ConfigManager configManager;
    public static KeyBinding clickGuiKey;

    /** Tracks previous key state for module keybinds. */
    private static final Map<Integer, Boolean> KEY_STATES = new HashMap<>();

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        moduleManager = new ModuleManager();
        configManager = new ConfigManager();

        // Right Shift = ClickGUI
        clickGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.singlecheat.clickgui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.singlecheat"
        ));

        // Load config first so module state exists before the first tick
        configManager.load();

        // ESP / Tracers world rendering
        ESPRenderer.init();

        // Chat commands (.toggle, .bind, .list, .help)
        CommandManager.register();

        // ---- Main tick loop ----
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            // HARD SINGLE-PLAYER LOCK: nothing runs in multiplayer.
            if (!SinglePlayerGuard.isSinglePlayer()) {
                SinglePlayerGuard.enforceSinglePlayer();
                return;
            }

            while (clickGuiKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ClickGuiScreen());
                }
            }

            handleModuleKeybinds(client);
            moduleManager.tick();
        });

        // HUD: watermark + ArrayList + single-player badge
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.options.hudHidden) return;
            if (client.player == null || client.world == null) return;
            HudRenderer.render(drawContext);
        });

        // Save when leaving a world
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> configManager.save());

        // Save on JVM exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                configManager.save();
            } catch (Throwable ignored) {
            }
        }, "SingleCheat-ConfigSave"));

        System.out.println("[SingleCheat] Loaded v" + VERSION + " - SINGLE PLAYER ONLY");
    }

    /** Edge-triggered module keybinds (ignored while a screen is open). */
    private static void handleModuleKeybinds(MinecraftClient client) {
        if (client.currentScreen != null) {
            KEY_STATES.clear();
            return;
        }
        long handle = client.getWindow().getHandle();

        for (Module module : moduleManager.getModules()) {
            int key = module.getKeybind();
            if (key == -1) continue;

            boolean down = InputUtil.isKeyPressed(handle, key);
            boolean wasDown = KEY_STATES.getOrDefault(key, false);
            KEY_STATES.put(key, down);

            if (down && !wasDown) {
                module.toggle();
            }
        }
    }

    public static boolean isSinglePlayer() {
        return SinglePlayerGuard.isSinglePlayer();
    }
}
