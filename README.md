# SingleCheat

A Fabric cheat client for **Minecraft 1.21.4** that is hard-locked to **single player**.
Open a world, press **Right Shift**, and everything is there. Join any remote server and
every module shuts off instantly and refuses to turn back on.

> 🔒 **Single-player only, by construction** — this is not a warning label, it is enforced in three places:
> the module base class, the client tick loop, and every mixin.

---

## Contents

- [Feature list](#feature-list)
- [Controls](#controls)
- [Commands](#commands)
- [Building](#building)
- [Installing](#installing)
- [How the single-player lock works](#how-the-single-player-lock-works)
- [Project layout](#project-layout)
- [Config file](#config-file)
- [Notes & limitations](#notes--limitations)

---

## Feature list

22 modules across five categories.

### Movement
| Module | Description | Notable settings |
|---|---|---|
| **Fly** | Fly in survival | Mode (Vanilla / Creative / Jetpack), Speed, Vertical Speed |
| **Speed** | Faster ground movement | Mode (Vanilla / Strafe / Hop), Multiplier |
| **NoFall** | No fall damage | Mode (Packet / Spoof / Zero) |
| **Sprint** | Always sprinting | Omni-Sprint, Keep Sprint |
| **Step** | Step up blocks instantly | Height 0.5 – 5 blocks |
| **Jesus** | Walk on water | Sink (hold sneak) |
| **Timer** | Speeds up world simulation | Speed 0.1× – 5× |

### Combat
| Module | Description | Notable settings |
|---|---|---|
| **KillAura** | Attacks the nearest entity | Range, Delay, Targets (All/Hostile/Passive/Players), Rotate |
| **AutoTotem** | Moves a totem to the offhand when low | Health Threshold |
| **Criticals** | Every hit is a critical | Mode (Jump / MiniJump) |
| **TriggerBot** | Attacks whatever your crosshair is on | Delay |

### Render
| Module | Description | Notable settings |
|---|---|---|
| **ESP** | Boxes through walls: players, mobs, items, containers | Range, per-type colors |
| **XRay** | Ores visible through terrain | Per-ore toggles |
| **Fullbright** | Maximum brightness (night vision based) | Night Vision |
| **Tracers** | Lines from your eyes to entities and containers | Range, colors |
| **Freecam** | Detach and fly the camera | Speed |
| **NoHurtCam** | No camera shake when damaged | – |

### Player
| Module | Description | Notable settings |
|---|---|---|
| **Scaffold** | Places blocks under you | Mode, Auto Switch, Eagle |
| **Nuker** | Breaks blocks around you | Range, Delay |
| **FastBreak** | Breaks blocks faster | Multiplier |
| **AutoEat** | Eats when hungry | Hunger Threshold |

### Misc
| Module | Description | Notable settings |
|---|---|---|
| **AntiAFK** | Jumps and turns to look active | Interval |

---

## Controls

| Action | Input |
|---|---|
| Open / close the ClickGUI | **Right Shift** |
| Toggle a module | Left click it in the GUI |
| Show / hide a module's settings | Right click it |
| Bind a key to a module | Middle click it (defaults to `R`), or `.bind <module> <key>` |
| Change a setting | Left click = increase / toggle, right click = decrease |
| Move a category panel | Drag its header |
| Collapse a category | Right click its header |
| Toggle a bound module in game | Press its bound key |

---

## Commands

Type these in chat while in a single-player world:

| Command | Effect |
|---|---|
| `.help` | List available commands |
| `.list` | All modules, green = enabled |
| `.toggle <module>` | Toggle a module (alias `.t`) |
| `.bind <module> <key>` | Bind a key, e.g. `.bind fly R`, `.bind fly F6`, `.bind fly none` |

---

## Building

Requirements: **JDK 21** and an internet connection for the first build (Gradle pulls
Minecraft, Yarn mappings and the Fabric API).

```bash
git clone https://github.com/parkymatt26-tech/ai.git
cd ai
./gradlew build          # Windows: gradlew.bat build
```

The finished mod lands in `build/libs/singlecheat-client-1.0.0.jar`.

Useful tasks:

```bash
./gradlew runClient      # launch a development client
./gradlew genSources     # decompile Minecraft into the IDE
```

---

## Installing

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for **Minecraft 1.21.4**.
2. Install [Fabric API](https://modrinth.com/mod/fabric-api) (`0.119.2+1.21.4` or newer for 1.21.4).
3. Copy `singlecheat-client-1.0.0.jar` into `.minecraft/mods/`.
4. Start the game, create or open a **single-player world**.
5. Press **Right Shift**.

---

## How the single-player lock works

```java
// SinglePlayerGuard.isSinglePlayer()
client.isInSingleplayer()          // integrated server running
|| client.getServer() != null      // integrated server object present
|| connection address is null/local
```

Three independent layers enforce it:

1. **`Module#setEnabled(true)`** returns immediately and prints *"blocked: single-player only"*
   if the guard fails. A module physically cannot be enabled in multiplayer, including from
   the GUI, a keybind, a command, or a config file.
2. **`Module#tick()`** re-checks the guard every tick and force-disables itself.
3. **`CheatClient`'s tick loop** calls `SinglePlayerGuard.enforceSinglePlayer()` before doing
   anything else, which force-disables every module without chat spam, and returns early so
   no module, keybind, or GUI logic runs at all.

The mixins (NoFall, XRay, NoHurtCam) and the ESP renderer each check the same guard, so
nothing is drawn or spoofed in multiplayer either. The HUD switches to a red
**MULTIPLAYER – LOCKED** badge and lists no modules.

A LAN world **you** host counts as single player (it runs an integrated server). Joining
someone else's LAN world or any remote server does not.

---

## Project layout

```
src/main/java/com/singlecheat/client/
├── CheatClient.java              entrypoint, tick loop, keybinds, HUD hook
├── command/CommandManager.java   chat commands
├── config/ConfigManager.java     JSON save / load
├── gui/
│   ├── ClickGuiScreen.java       draggable panels, settings editor
│   └── HudRenderer.java          watermark, module list, SP badge
├── module/
│   ├── Module.java               base class, guard-checked enable / tick
│   ├── ModuleCategory.java
│   ├── ModuleManager.java
│   └── modules/{movement,combat,render,player,misc}/
├── setting/                      Boolean / Number / Mode / Color settings
├── mixin/                        NoFall, NoHurtCam, XRay (3 hooks)
└── utils/
    ├── SinglePlayerGuard.java    the lock
    └── ESPRenderer.java          ESP boxes + tracers
```

---

## Config file

State is stored in `config/singlecheat.json` inside your Minecraft directory:

```json
{
  "version": "1.0.0",
  "modules": [
    {
      "name": "Fly",
      "enabled": true,
      "keybind": 82,
      "settings": { "Speed": 2.0, "Mode": "Vanilla" }
    }
  ]
}
```

Saved on world exit and on game shutdown, loaded at startup. Enabling a module from the
config is still refused if you are not in single player.

---

## Notes & limitations

- Module behaviour is implemented client-side against the integrated server; no packet
  exploits aimed at remote anti-cheats exist anywhere in this codebase.
- **Timer** accelerates entity simulation only (it cannot change the 20 TPS tick loop).
- **XRay** hides a fixed set of common terrain blocks; it is a shell/ore X-ray, not a
  full model replacement.
- **Freecam** moves the player entity with flight and restores your position on disable.
- Mixin targets are written against Yarn `1.21.4+build.8` and are marked `require = 0`,
  so a mapping mismatch degrades a single feature instead of crashing the game.

---

## License

MIT — see [LICENSE](LICENSE). Not affiliated with Mojang or Microsoft.
