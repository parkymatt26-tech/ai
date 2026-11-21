package com.singlecheat.client.gui;

import com.singlecheat.client.CheatClient;
import com.singlecheat.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

/**
 * In-game HUD: watermark, module ArrayList and the single-player badge.
 */
public final class HudRenderer {
    private static final int ACCENT = 0xFF00E5FF;
    private static final int PANEL_BG = 0xB0121212;

    private HudRenderer() {}

    public static void render(DrawContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        TextRenderer tr = client.textRenderer;
        boolean singlePlayer = CheatClient.isSinglePlayer();

        // ---- Watermark (top left) ----
        String title = "SingleCheat";
        String version = " v" + CheatClient.VERSION;
        int titleWidth = tr.getWidth(title);
        int versionWidth = tr.getWidth(version);
        int boxWidth = titleWidth + versionWidth + 12;

        ctx.fill(2, 2, 2 + boxWidth, 14, PANEL_BG);
        ctx.fill(2, 2, 4, 14, ACCENT);
        ctx.drawTextWithShadow(tr, title, 7, 5, ACCENT);
        ctx.drawTextWithShadow(tr, version, 7 + titleWidth, 5, 0xFF9E9E9E);

        // ---- Single-player badge ----
        String badge = singlePlayer ? "SINGLE-PLAYER" : "MULTIPLAYER - LOCKED";
        int badgeColor = singlePlayer ? 0xFF4CAF50 : 0xFFE53935;
        int badgeWidth = tr.getWidth(badge);
        ctx.fill(2, 16, 2 + badgeWidth + 8, 28, PANEL_BG);
        ctx.fill(2, 16, 4, 28, badgeColor);
        ctx.drawTextWithShadow(tr, badge, 7, 19, badgeColor);

        // ---- ArrayList (top right) ----
        List<Module> enabled = CheatClient.moduleManager.getEnabled();
        int y = 2;
        for (Module module : enabled) {
            String text = module.getName();
            int width = tr.getWidth(text);
            int right = ctx.getScaledWindowWidth() - 3;
            int left = right - width - 8;

            ctx.fill(left, y, right, y + 11, PANEL_BG);
            ctx.fill(right - 2, y, right, y + 11, ACCENT);
            ctx.drawTextWithShadow(tr, text, left + 4, y + 2, 0xFFEEEEEE);
            y += 12;
        }

        // ---- Module counter under the list ----
        if (!enabled.isEmpty()) {
            String counter = enabled.size() + " active";
            int width = tr.getWidth(counter);
            ctx.drawTextWithShadow(tr, counter,
                    ctx.getScaledWindowWidth() - width - 6, y + 1, 0xFF757575);
        }

        // ---- Bottom right ----
        String info = "FPS " + client.getCurrentFps() + "  |  " + (singlePlayer ? "cheats enabled" : "cheats disabled");
        int infoWidth = tr.getWidth(info);
        ctx.drawTextWithShadow(tr, info,
                ctx.getScaledWindowWidth() - infoWidth - 4,
                ctx.getScaledWindowHeight() - 12,
                singlePlayer ? 0xFF9E9E9E : 0xFFE53935);
    }
}
