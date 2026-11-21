package com.singlecheat.client.command;

import com.singlecheat.client.CheatClient;
import com.singlecheat.client.module.Module;
import com.singlecheat.client.utils.SinglePlayerGuard;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;


/**
 * Chat commands with "." prefix. Single-player only.
 * Examples:
 *   .toggle fly
 *   .bind fly R
 *   .list
 *   .help
 */
public final class CommandManager {
    public static final String PREFIX = ".";

    private CommandManager() {}

    public static void register() {
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (!message.startsWith(PREFIX)) return true;
            handle(message);
            return false; // cancel sending to server
        });
    }

    public static void handle(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if (!SinglePlayerGuard.isSinglePlayer()) {
            send(client, "§cCommands are single-player only!");
            return;
        }

        String[] parts = message.substring(PREFIX.length()).trim().split("\\s+");
        if (parts.length == 0) return;
        String cmd = parts[0].toLowerCase();

        switch (cmd) {
            case "toggle", "t" -> {
                if (parts.length < 2) { send(client, "§cUsage: .toggle <module>"); return; }
                CheatClient.moduleManager.getByName(parts[1]).ifPresentOrElse(
                        Module::toggle,
                        () -> send(client, "§cModule not found: " + parts[1])
                );
            }
            case "bind" -> {
                if (parts.length < 3) { send(client, "§cUsage: .bind <module> <key|none>"); return; }
                CheatClient.moduleManager.getByName(parts[1]).ifPresentOrElse(module -> {
                    if (parts[2].equalsIgnoreCase("none")) {
                        module.setKeybind(-1);
                        send(client, "§aUnbound " + module.getName());
                    } else {
                        int key = parseKey(parts[2]);
                        if (key == -1) {
                            send(client, "§cUnknown key: " + parts[2]);
                        } else {
                            module.setKeybind(key);
                            send(client, "§aBound " + module.getName() + " to " + parts[2].toUpperCase());
                        }
                    }
                }, () -> send(client, "§cModule not found: " + parts[1]));
            }
            case "list" -> {
                StringBuilder sb = new StringBuilder("§bModules: §f");
                for (Module m : CheatClient.moduleManager.getModules()) {
                    sb.append(m.isEnabled() ? "§a" : "§7").append(m.getName()).append("§f, ");
                }
                send(client, sb.toString());
            }
            case "help" -> {
                send(client, "§bSingleCheat Commands:");
                send(client, "§f.toggle <module> §8- toggle a module");
                send(client, "§f.bind <module> <key> §8- set keybind");
                send(client, "§f.list §8- list all modules");
                send(client, "§f.help §8- this help");
            }
            default -> send(client, "§cUnknown command. Try .help");
        }
    }

    /** Small, dependency-free key name -> GLFW key code parser. */
    public static int parseKey(String raw) {
        String name = raw.trim().toUpperCase();
        if (name.length() == 1) {
            char c = name.charAt(0);
            if (c >= 'A' && c <= 'Z') return GLFW.GLFW_KEY_A + (c - 'A');
            if (c >= '0' && c <= '9') return GLFW.GLFW_KEY_0 + (c - '0');
        }
        if (name.matches("F\\d{1,2}")) {
            int n = Integer.parseInt(name.substring(1));
            if (n >= 1 && n <= 25) return GLFW.GLFW_KEY_F1 + (n - 1);
            return -1;
        }
        return switch (name) {
            case "SPACE" -> GLFW.GLFW_KEY_SPACE;
            case "TAB" -> GLFW.GLFW_KEY_TAB;
            case "LSHIFT", "LEFTSHIFT" -> GLFW.GLFW_KEY_LEFT_SHIFT;
            case "RSHIFT", "RIGHTSHIFT" -> GLFW.GLFW_KEY_RIGHT_SHIFT;
            case "LCTRL", "LEFTCTRL" -> GLFW.GLFW_KEY_LEFT_CONTROL;
            case "RCTRL", "RIGHTCTRL" -> GLFW.GLFW_KEY_RIGHT_CONTROL;
            case "LALT", "LEFTALT" -> GLFW.GLFW_KEY_LEFT_ALT;
            case "RALT", "RIGHTALT" -> GLFW.GLFW_KEY_RIGHT_ALT;
            case "GRAVE", "BACKTICK" -> GLFW.GLFW_KEY_GRAVE_ACCENT;
            case "MINUS" -> GLFW.GLFW_KEY_MINUS;
            case "EQUAL", "EQUALS" -> GLFW.GLFW_KEY_EQUAL;
            case "UP" -> GLFW.GLFW_KEY_UP;
            case "DOWN" -> GLFW.GLFW_KEY_DOWN;
            case "LEFT" -> GLFW.GLFW_KEY_LEFT;
            case "RIGHT" -> GLFW.GLFW_KEY_RIGHT;
            case "MOUSE1", "LMB" -> 0;
            case "MOUSE2", "RMB" -> 1;
            case "MOUSE3", "MMB" -> 2;
            case "NONE" -> -1;
            default -> -1;
        };
    }

    private static void send(MinecraftClient client, String msg) {
        client.player.sendMessage(Text.of("§7[§bSingleCheat§7] " + msg), false);
    }
}
