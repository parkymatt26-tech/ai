package com.singlecheat.client.module;

public enum ModuleCategory {
    MOVEMENT("Movement", "§a"),
    COMBAT("Combat", "§c"),
    RENDER("Render", "§b"),
    PLAYER("Player", "§e"),
    MISC("Misc", "§d");

    public final String displayName;
    public final String color;

    ModuleCategory(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }
}
