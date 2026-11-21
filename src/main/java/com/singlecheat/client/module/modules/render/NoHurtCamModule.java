package com.singlecheat.client.module.modules.render;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;

public class NoHurtCamModule extends Module {
    public static NoHurtCamModule INSTANCE;

    public NoHurtCamModule() {
        super("NoHurtCam", "No camera shake when hurt", ModuleCategory.RENDER);
        INSTANCE = this;
    }
}
