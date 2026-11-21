package com.singlecheat.client.module.modules.render;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.BooleanSetting;
import com.singlecheat.client.setting.ColorSetting;
import com.singlecheat.client.setting.NumberSetting;

public class TracersModule extends Module {
    public static TracersModule INSTANCE;

    private final NumberSetting range = register(new NumberSetting("Range", "Tracer range", 64.0, 8.0, 256.0, 1.0));
    private final BooleanSetting players = register(new BooleanSetting("Players", "Trace players", true));
    private final BooleanSetting mobs = register(new BooleanSetting("Mobs", "Trace mobs", false));
    private final BooleanSetting chests = register(new BooleanSetting("Chests", "Trace chests", true));
    private final ColorSetting color = register(new ColorSetting("Color", "Tracer color", 0x00FFFF));

    public TracersModule() {
        super("Tracers", "Lines to entities through walls", ModuleCategory.RENDER);
        INSTANCE = this;
    }

    public double getRange() { return range.getValue(); }
    public boolean showPlayers() { return players.getValue(); }
    public boolean showMobs() { return mobs.getValue(); }
    public boolean showChests() { return chests.getValue(); }
    public int getColor() { return color.getValue(); }
}
