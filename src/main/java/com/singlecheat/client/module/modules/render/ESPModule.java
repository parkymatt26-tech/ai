package com.singlecheat.client.module.modules.render;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.BooleanSetting;
import com.singlecheat.client.setting.ColorSetting;
import com.singlecheat.client.setting.NumberSetting;

public class ESPModule extends Module {
    public static ESPModule INSTANCE;

    private final NumberSetting range = register(new NumberSetting("Range", "ESP range", 64.0, 8.0, 256.0, 1.0));
    private final BooleanSetting players = register(new BooleanSetting("Players", "Show players", true));
    private final BooleanSetting mobs = register(new BooleanSetting("Mobs", "Show mobs", true));
    private final BooleanSetting items = register(new BooleanSetting("Items", "Show dropped items", true));
    private final BooleanSetting chests = register(new BooleanSetting("Chests", "Show chests / containers", true));
    private final ColorSetting playerColor = register(new ColorSetting("Player Color", "Player box color", 0x00FF00));
    private final ColorSetting mobColor = register(new ColorSetting("Mob Color", "Mob box color", 0xFF0000));
    private final ColorSetting itemColor = register(new ColorSetting("Item Color", "Item box color", 0xFFFF00));
    private final ColorSetting chestColor = register(new ColorSetting("Chest Color", "Chest box color", 0xFFAA00));

    public ESPModule() {
        super("ESP", "Entity / chest boxes through walls", ModuleCategory.RENDER);
        INSTANCE = this;
    }

    public double getRange() { return range.getValue(); }
    public boolean showPlayers() { return players.getValue(); }
    public boolean showMobs() { return mobs.getValue(); }
    public boolean showItems() { return items.getValue(); }
    public boolean showChests() { return chests.getValue(); }
    public int getPlayerColor() { return playerColor.getValue(); }
    public int getMobColor() { return mobColor.getValue(); }
    public int getItemColor() { return itemColor.getValue(); }
    public int getChestColor() { return chestColor.getValue(); }
}
