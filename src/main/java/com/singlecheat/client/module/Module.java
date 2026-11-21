package com.singlecheat.client.module;

import com.singlecheat.client.setting.Setting;
import com.singlecheat.client.utils.SinglePlayerGuard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all cheat modules.
 * Every module checks SinglePlayerGuard before ticking.
 */
public abstract class Module {
    protected final String name;
    protected final String description;
    protected final ModuleCategory category;
    protected boolean enabled = false;
    protected int keybind = -1; // GLFW key code, -1 = none
    protected final List<Setting<?>> settings = new ArrayList<>();

    /** Resolved lazily so modules constructed during client init never hold null. */
    protected MinecraftClient mc = MinecraftClient.getInstance();

    public Module(String name, String description, ModuleCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    /** Re-resolves the client instance (called by the module manager on register). */
    public void refreshClient() {
        MinecraftClient current = MinecraftClient.getInstance();
        if (current != null) {
            this.mc = current;
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        // HARD LOCK: never enable in multiplayer
        if (enabled && !SinglePlayerGuard.isSinglePlayer()) {
            if (mc.player != null) {
                mc.player.sendMessage(Text.of("§c[SingleCheat] §f" + name + " blocked: single-player only!"), false);
            }
            return;
        }
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) {
            onEnable();
            notify("§a" + name + " §fON");
        } else {
            onDisable();
            notify("§c" + name + " §fOFF");
        }
    }

    /**
     * Disables the module without any chat feedback. Used by the single-player
     * guard when it has to shut everything down at once.
     */
    public void forceDisable() {
        if (!this.enabled) return;
        this.enabled = false;
        onDisable();
    }

    private void notify(String msg) {
        if (mc.player != null && mc.world != null) {
            mc.player.sendMessage(Text.of("§7[§bSingleCheat§7] " + msg), false);
        }
    }

    public void tick() {
        if (!enabled) return;
        if (!SinglePlayerGuard.canUseCheats()) {
            setEnabled(false);
            return;
        }
        onTick();
    }

    protected void onEnable() {}
    protected void onDisable() {}
    protected void onTick() {}

    protected <T extends Setting<?>> T register(T setting) {
        settings.add(setting);
        return setting;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public ModuleCategory getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public int getKeybind() { return keybind; }
    public void setKeybind(int keybind) { this.keybind = keybind; }
    public List<Setting<?>> getSettings() { return settings; }
}
