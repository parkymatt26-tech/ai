package com.singlecheat.client.module;

import com.singlecheat.client.module.modules.combat.*;
import com.singlecheat.client.module.modules.misc.*;
import com.singlecheat.client.module.modules.movement.*;
import com.singlecheat.client.module.modules.player.*;
import com.singlecheat.client.module.modules.render.*;

import java.util.*;
import java.util.stream.Collectors;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();
    private final Map<String, Module> byName = new HashMap<>();

    public ModuleManager() {
        // Movement
        register(new FlyModule());
        register(new SpeedModule());
        register(new NoFallModule());
        register(new SprintModule());
        register(new StepModule());
        register(new JesusModule());
        register(new TimerModule());

        // Combat
        register(new KillAuraModule());
        register(new AutoTotemModule());
        register(new CriticalsModule());
        register(new TriggerBotModule());

        // Render
        register(new ESPModule());
        register(new XRayModule());
        register(new FullbrightModule());
        register(new TracersModule());
        register(new FreecamModule());
        register(new NoHurtCamModule());

        // Player
        register(new ScaffoldModule());
        register(new NukerModule());
        register(new FastBreakModule());
        register(new AutoEatModule());

        // Misc
        register(new AntiAFKModule());
    }

    private void register(Module module) {
        module.refreshClient();
        modules.add(module);
        byName.put(module.getName().toLowerCase(), module);
    }

    public void tick() {
        for (Module m : modules) {
            m.tick();
        }
    }

    public void disableAll() {
        for (Module m : modules) {
            m.forceDisable();
        }
    }

    public List<Module> getModules() { return Collections.unmodifiableList(modules); }

    public List<Module> getByCategory(ModuleCategory cat) {
        return modules.stream().filter(m -> m.getCategory() == cat).collect(Collectors.toList());
    }

    public Optional<Module> getByName(String name) {
        return Optional.ofNullable(byName.get(name.toLowerCase()));
    }

    public <T extends Module> Optional<T> get(Class<T> clazz) {
        return modules.stream().filter(clazz::isInstance).map(clazz::cast).findFirst();
    }

    public List<Module> getEnabled() {
        return modules.stream().filter(Module::isEnabled).collect(Collectors.toList());
    }
}
