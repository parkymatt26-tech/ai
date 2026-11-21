package com.singlecheat.client.module.modules.render;

import com.singlecheat.client.module.Module;
import com.singlecheat.client.module.ModuleCategory;
import com.singlecheat.client.setting.BooleanSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.resource.ResourceManager;

import java.lang.reflect.Method;
import java.util.Set;

public class XRayModule extends Module {
    public static XRayModule INSTANCE;

    /** Terrain that gets hidden while XRay is on. Ores are never in this set. */
    private static final Set<Block> HIDDEN_BLOCKS = Set.of(
            Blocks.STONE, Blocks.DEEPSLATE, Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.COBBLESTONE,
            Blocks.GRAVEL, Blocks.SAND, Blocks.SANDSTONE, Blocks.RED_SANDSTONE,
            Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.TUFF, Blocks.CALCITE,
            Blocks.NETHERRACK, Blocks.BASALT, Blocks.BLACKSTONE, Blocks.SMOOTH_BASALT,
            Blocks.END_STONE, Blocks.PODZOL, Blocks.MYCELIUM, Blocks.CLAY,
            Blocks.SNOW_BLOCK, Blocks.MOSS_BLOCK, Blocks.DRIPSTONE_BLOCK,
            Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.TERRACOTTA,
            Blocks.NETHER_QUARTZ_ORE, Blocks.NETHER_GOLD_ORE
    );

    private final BooleanSetting diamonds = register(new BooleanSetting("Diamonds", "Show diamond ore", true));
    private final BooleanSetting gold = register(new BooleanSetting("Gold", "Show gold ore", true));
    private final BooleanSetting iron = register(new BooleanSetting("Iron", "Show iron ore", true));
    private final BooleanSetting coal = register(new BooleanSetting("Coal", "Show coal ore", false));
    private final BooleanSetting redstone = register(new BooleanSetting("Redstone", "Show redstone ore", true));
    private final BooleanSetting lapis = register(new BooleanSetting("Lapis", "Show lapis ore", true));
    private final BooleanSetting emerald = register(new BooleanSetting("Emerald", "Show emerald ore", true));
    private final BooleanSetting ancientDebris = register(new BooleanSetting("Ancient Debris", "Show ancient debris", true));

    public XRayModule() {
        super("XRay", "See ores through walls", ModuleCategory.RENDER);
        INSTANCE = this;
    }

    @Override
    protected void onEnable() {
        reloadChunks();
    }

    @Override
    protected void onDisable() {
        reloadChunks();
    }

    /** Called by the mixins. True = do not render / do not cull. */
    public boolean shouldHide(BlockState state) {
        if (!isEnabled()) return false;
        Block block = state.getBlock();
        if (isOreVisible(block)) return false;
        return HIDDEN_BLOCKS.contains(block);
    }

    /** True if this block should stay visible while XRay is on. */
    public boolean isOreVisible(Block block) {
        if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) return diamonds.getValue();
        if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) return gold.getValue();
        if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) return iron.getValue();
        if (block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE) return coal.getValue();
        if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) return redstone.getValue();
        if (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE) return lapis.getValue();
        if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) return emerald.getValue();
        if (block == Blocks.ANCIENT_DEBRIS) return ancientDebris.getValue();
        if (block == Blocks.NETHER_QUARTZ_ORE) return gold.getValue();
        return false;
    }

    /** Rebuilds chunk meshes so the change is visible immediately. */
    private void reloadChunks() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.worldRenderer == null) return;
        try {
            Method noArg = WorldRenderer.class.getMethod("reload");
            noArg.invoke(client.worldRenderer);
            return;
        } catch (Throwable ignored) {
            // newer mappings take a ResourceManager
        }
        try {
            Method withArg = WorldRenderer.class.getMethod("reload", ResourceManager.class);
            withArg.invoke(client.worldRenderer, client.getResourceManager());
        } catch (Throwable ignored) {
        }
    }
}
