package com.singlecheat.client.gui;

import com.singlecheat.client.CheatClient;
import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.BooleanSetting;
import com.singlecheat.client.setting.ColorSetting;
import com.singlecheat.client.setting.ModeSetting;
import com.singlecheat.client.setting.NumberSetting;
import com.singlecheat.client.setting.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClickGUI, opened with Right Shift.
 *
 * <ul>
 *   <li>Left click a module: toggle</li>
 *   <li>Right click a module: show / hide its settings</li>
 *   <li>Middle click a module: bind / unbind (R)</li>
 *   <li>Left / right click a setting: increase / decrease or cycle</li>
 *   <li>Drag a category header: move the panel</li>
 *   <li>Right click a category header: collapse / expand</li>
 * </ul>
 */
public class ClickGuiScreen extends Screen {
    private static final int PANEL_WIDTH = 150;
    private static final int HEADER_HEIGHT = 18;
    private static final int ROW_HEIGHT = 16;
    private static final int SETTING_HEIGHT = 14;

    private static final int BG_PANEL = 0xF0141419;
    private static final int BG_HEADER = 0xF01E1E28;
    private static final int BG_ROW = 0xC01A1A22;
    private static final int BG_ROW_ENABLED = 0xE0007A8A;
    private static final int BG_ROW_HOVER = 0x40FFFFFF;
    private static final int BG_SETTING = 0xC0101016;
    private static final int ACCENT = 0xFF00E5FF;
    private static final int TEXT_DIM = 0xFF8A8A8A;
    private static final int TEXT = 0xFFEDEDED;

    private static final class Panel {
        final ModuleCategory category;
        int x;
        int y;
        boolean expanded = true;
        boolean dragging;
        int dragOffsetX;
        int dragOffsetY;

        Panel(ModuleCategory category, int x, int y) {
            this.category = category;
            this.x = x;
            this.y = y;
        }
    }

    private final List<Panel> panels = new ArrayList<>();
    private final Map<String, Boolean> expandedModules = new HashMap<>();
    private String hoverDescription = null;

    public ClickGuiScreen() {
        super(Text.of("SingleCheat"));
        int x = 12;
        for (ModuleCategory category : ModuleCategory.values()) {
            panels.add(new Panel(category, x, 24));
            x += PANEL_WIDTH + 10;
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        hoverDescription = null;

        ctx.fill(0, 0, width, height, 0x6A000000);

        // Title bar
        ctx.fill(0, 0, width, 20, 0xE0101015);
        ctx.fill(0, 19, width, 20, ACCENT);
        ctx.drawTextWithShadow(textRenderer, "§bSingleCheat §7v" + CheatClient.VERSION, 8, 6, TEXT);
        String hint = "§7Right Shift §8- §7close   §8|   §7.help §8- §7commands";
        ctx.drawTextWithShadow(textRenderer, hint, width - textRenderer.getWidth(hint) - 8, 6, TEXT_DIM);

        for (Panel panel : panels) {
            renderPanel(ctx, panel, mouseX, mouseY);
        }

        if (hoverDescription != null) {
            int w = textRenderer.getWidth(hoverDescription) + 10;
            ctx.fill(6, height - 20, 6 + w, height - 6, 0xE0101015);
            ctx.drawTextWithShadow(textRenderer, hoverDescription, 11, height - 16, ACCENT);
        }
    }

    private void renderPanel(DrawContext ctx, Panel panel, int mouseX, int mouseY) {
        List<Module> modules = CheatClient.moduleManager.getByCategory(panel.category);

        // Panel background behind the whole body
        int bodyHeight = HEADER_HEIGHT;
        if (panel.expanded) {
            for (Module module : modules) {
                bodyHeight += ROW_HEIGHT;
                if (expandedModules.getOrDefault(module.getName(), false)) {
                    bodyHeight += module.getSettings().size() * SETTING_HEIGHT;
                }
            }
        }
        ctx.fill(panel.x, panel.y, panel.x + PANEL_WIDTH, panel.y + bodyHeight, BG_PANEL);

        // Header
        boolean headerHover = inside(mouseX, mouseY, panel.x, panel.y, PANEL_WIDTH, HEADER_HEIGHT);
        ctx.fill(panel.x, panel.y, panel.x + PANEL_WIDTH, panel.y + HEADER_HEIGHT,
                headerHover ? 0xF0282836 : BG_HEADER);
        ctx.fill(panel.x, panel.y, panel.x + 3, panel.y + HEADER_HEIGHT, ACCENT);
        ctx.drawTextWithShadow(textRenderer, panel.category.displayName,
                panel.x + 8, panel.y + 5, TEXT);
        ctx.drawTextWithShadow(textRenderer, panel.expanded ? "-" : "+",
                panel.x + PANEL_WIDTH - 12, panel.y + 5, TEXT_DIM);

        if (!panel.expanded) {
            ctx.drawBorder(panel.x, panel.y, PANEL_WIDTH, bodyHeight, 0x6000E5FF);
            return;
        }

        int y = panel.y + HEADER_HEIGHT;
        for (Module module : modules) {
            boolean enabled = module.isEnabled();
            boolean hover = inside(mouseX, mouseY, panel.x, y, PANEL_WIDTH, ROW_HEIGHT);

            ctx.fill(panel.x, y, panel.x + PANEL_WIDTH, y + ROW_HEIGHT, enabled ? BG_ROW_ENABLED : BG_ROW);
            if (hover) {
                ctx.fill(panel.x, y, panel.x + PANEL_WIDTH, y + ROW_HEIGHT, BG_ROW_HOVER);
                hoverDescription = module.getDescription();
            }
            ctx.fill(panel.x, y, panel.x + 2, y + ROW_HEIGHT, enabled ? ACCENT : 0xFF3A3A46);
            ctx.drawTextWithShadow(textRenderer, module.getName(), panel.x + 8, y + 4, enabled ? TEXT : TEXT_DIM);

            String bind = keybindName(module.getKeybind());
            if (bind != null) {
                ctx.drawTextWithShadow(textRenderer, "§8[" + bind + "]",
                        panel.x + PANEL_WIDTH - textRenderer.getWidth(bind) - 10, y + 4, TEXT_DIM);
            }
            y += ROW_HEIGHT;

            if (!expandedModules.getOrDefault(module.getName(), false)) continue;

            for (Setting<?> setting : module.getSettings()) {
                boolean settingHover = inside(mouseX, mouseY, panel.x, y, PANEL_WIDTH, SETTING_HEIGHT);
                ctx.fill(panel.x + 4, y, panel.x + PANEL_WIDTH - 4, y + SETTING_HEIGHT,
                        settingHover ? 0xE01C1C26 : BG_SETTING);

                String name = setting.getName();
                String value = setting.getDisplayValue();
                ctx.drawTextWithShadow(textRenderer, "§8" + name, panel.x + 9, y + 3, TEXT_DIM);
                ctx.drawTextWithShadow(textRenderer, "§f" + value,
                        panel.x + PANEL_WIDTH - textRenderer.getWidth(value) - 10, y + 3, TEXT);
                if (settingHover) {
                    hoverDescription = setting.getDescription();
                }
                y += SETTING_HEIGHT;
            }
        }

        ctx.drawBorder(panel.x, panel.y, PANEL_WIDTH, bodyHeight, 0x6000E5FF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Panel panel : panels) {
            if (inside(mouseX, mouseY, panel.x, panel.y, PANEL_WIDTH, HEADER_HEIGHT)) {
                if (button == 0) {
                    panel.dragging = true;
                    panel.dragOffsetX = (int) mouseX - panel.x;
                    panel.dragOffsetY = (int) mouseY - panel.y;
                } else if (button == 1) {
                    panel.expanded = !panel.expanded;
                }
                return true;
            }

            if (!panel.expanded) continue;

            int y = panel.y + HEADER_HEIGHT;
            for (Module module : CheatClient.moduleManager.getByCategory(panel.category)) {
                if (inside(mouseX, mouseY, panel.x, y, PANEL_WIDTH, ROW_HEIGHT)) {
                    if (button == 0) {
                        module.toggle();
                    } else if (button == 1) {
                        expandedModules.put(module.getName(),
                                !expandedModules.getOrDefault(module.getName(), false));
                    } else if (button == 2) {
                        module.setKeybind(module.getKeybind() == -1 ? GLFW.GLFW_KEY_R : -1);
                    }
                    return true;
                }
                y += ROW_HEIGHT;

                if (!expandedModules.getOrDefault(module.getName(), false)) continue;

                for (Setting<?> setting : module.getSettings()) {
                    if (inside(mouseX, mouseY, panel.x, y, PANEL_WIDTH, SETTING_HEIGHT)) {
                        modify(setting, button);
                        return true;
                    }
                    y += SETTING_HEIGHT;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void modify(Setting<?> setting, int button) {
        if (setting instanceof BooleanSetting boolSetting) {
            boolSetting.toggle();
        } else if (setting instanceof NumberSetting numberSetting) {
            if (button == 0) numberSetting.increment(); else numberSetting.decrement();
        } else if (setting instanceof ModeSetting modeSetting) {
            modeSetting.cycle();
        } else if (setting instanceof ColorSetting colorSetting) {
            int[] presets = {0xFF5555, 0x55FF55, 0x5555FF, 0xFFFF55, 0xFF55FF, 0x55FFFF, 0xFFFFFF, 0xFFAA00};
            int next = 0;
            for (int i = 0; i < presets.length; i++) {
                if (presets[i] == colorSetting.getValue()) {
                    next = (i + 1) % presets.length;
                    break;
                }
            }
            colorSetting.setValue(presets[next]);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (Panel panel : panels) {
            if (!panel.dragging) continue;
            panel.x = Math.max(0, Math.min(width - PANEL_WIDTH, (int) mouseX - panel.dragOffsetX));
            panel.y = Math.max(20, Math.min(height - HEADER_HEIGHT, (int) mouseY - panel.dragOffsetY));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Panel panel : panels) {
            panel.dragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        for (Module module : CheatClient.moduleManager.getModules()) {
            if (module.getKeybind() == keyCode) {
                module.toggle();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    private static String keybindName(int key) {
        if (key == -1) return null;
        String name = GLFW.glfwGetKeyName(key, 0);
        return name == null ? null : name.toUpperCase();
    }
}
