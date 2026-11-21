package com.singlecheat.client.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.singlecheat.client.CheatClient;
import com.singlecheat.client.module.modules.render.ESPModule;
import com.singlecheat.client.module.modules.render.TracersModule;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;

/**
 * ESP boxes + Tracers, drawn in world space each frame.
 * Everything is gated on the single-player guard as well as on module state.
 */
public final class ESPRenderer {
    private ESPRenderer() {}

    public static void init() {
        WorldRenderEvents.AFTER_ENTITIES.register(ESPRenderer::render);
    }

    private static void render(WorldRenderContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        if (!SinglePlayerGuard.isSinglePlayer()) return;

        ESPModule esp = CheatClient.moduleManager.get(ESPModule.class).orElse(null);
        TracersModule tracers = CheatClient.moduleManager.get(TracersModule.class).orElse(null);
        boolean espOn = esp != null && esp.isEnabled();
        boolean tracerOn = tracers != null && tracers.isEnabled();
        if (!espOn && !tracerOn) return;

        MatrixStack matrices = ctx.matrixStack();
        if (matrices == null) return;
        Vec3d camPos = ctx.camera().getPos();
        double range = Math.max(espOn ? esp.getRange() : 0, tracerOn ? tracers.getRange() : 0);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.lineWidth(1.5f);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Box search = client.player.getBoundingBox().expand(range);
        for (Entity entity : client.world.getEntitiesByClass(Entity.class, search, e -> e != client.player && e.isAlive())) {
            double dist = client.player.distanceTo(entity);

            int boxColor = -1;
            if (espOn && dist <= esp.getRange()) {
                if (entity instanceof PlayerEntity && esp.showPlayers()) boxColor = esp.getPlayerColor();
                else if (entity instanceof MobEntity && esp.showMobs()) boxColor = esp.getMobColor();
                else if (entity instanceof ItemEntity && esp.showItems()) boxColor = esp.getItemColor();
            }
            if (boxColor != -1) {
                drawBox(matrices, camPos, entity.getBoundingBox(), boxColor);
            }

            if (tracerOn && dist <= tracers.getRange()) {
                boolean player = entity instanceof PlayerEntity && tracers.showPlayers();
                boolean mob = entity instanceof LivingEntity && !(entity instanceof PlayerEntity) && tracers.showMobs();
                if (player || mob) {
                    drawTracer(matrices, camPos, entity, tracers.getColor());
                }
            }
        }

        // Containers (chests / barrels / shulkers) via loaded block entities
        if ((espOn && esp.showChests()) || (tracerOn && tracers.showChests())) {
            int chunkRadius = Math.max(1, (int) Math.ceil(range / 16.0));
            ChunkPos center = client.player.getChunkPos();
            for (int cx = -chunkRadius; cx <= chunkRadius; cx++) {
                for (int cz = -chunkRadius; cz <= chunkRadius; cz++) {
                    Chunk chunk = client.world.getChunk(center.x + cx, center.z + cz);
                    if (!(chunk instanceof WorldChunk worldChunk)) continue;
                    for (BlockEntity be : worldChunk.getBlockEntities().values()) {
                        BlockPos pos = be.getPos();
                        double dist = Vec3d.ofCenter(pos).distanceTo(client.player.getPos());
                        if (dist > range) continue;

                        BlockState state = be.getCachedState();
                        boolean container = state.isOf(Blocks.CHEST) || state.isOf(Blocks.TRAPPED_CHEST)
                                || state.isOf(Blocks.ENDER_CHEST) || state.isOf(Blocks.BARREL)
                                || state.getBlock() instanceof ShulkerBoxBlock;
                        if (!container) continue;

                        if (espOn && esp.showChests() && dist <= esp.getRange()) {
                            drawBox(matrices, camPos, new Box(pos), esp.getChestColor());
                        }
                        if (tracerOn && tracers.showChests() && dist <= tracers.getRange()) {
                            drawTracerTo(matrices, camPos, Vec3d.ofCenter(pos), tracers.getColor(), client);
                        }
                    }
                }
            }
        }

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void drawBox(MatrixStack matrices, Vec3d camPos, Box box, int color) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = 0.85f;

        matrices.push();
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance()
                .begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        double x1 = box.minX, y1 = box.minY, z1 = box.minZ;
        double x2 = box.maxX, y2 = box.maxY, z2 = box.maxZ;

        // bottom
        line(buffer, matrix, x1, y1, z1, x2, y1, z1, r, g, b, a);
        line(buffer, matrix, x2, y1, z1, x2, y1, z2, r, g, b, a);
        line(buffer, matrix, x2, y1, z2, x1, y1, z2, r, g, b, a);
        line(buffer, matrix, x1, y1, z2, x1, y1, z1, r, g, b, a);
        // top
        line(buffer, matrix, x1, y2, z1, x2, y2, z1, r, g, b, a);
        line(buffer, matrix, x2, y2, z1, x2, y2, z2, r, g, b, a);
        line(buffer, matrix, x2, y2, z2, x1, y2, z2, r, g, b, a);
        line(buffer, matrix, x1, y2, z2, x1, y2, z1, r, g, b, a);
        // verticals
        line(buffer, matrix, x1, y1, z1, x1, y2, z1, r, g, b, a);
        line(buffer, matrix, x2, y1, z1, x2, y2, z1, r, g, b, a);
        line(buffer, matrix, x2, y1, z2, x2, y2, z2, r, g, b, a);
        line(buffer, matrix, x1, y1, z2, x1, y2, z2, r, g, b, a);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        matrices.pop();
    }

    private static void drawTracer(MatrixStack matrices, Vec3d camPos, Entity target, int color) {
        Vec3d center = target.getPos().add(0, target.getHeight() / 2.0, 0);
        drawTracerTo(matrices, camPos, center, color, MinecraftClient.getInstance());
    }

    private static void drawTracerTo(MatrixStack matrices, Vec3d camPos, Vec3d target, int color, MinecraftClient client) {
        if (client.player == null) return;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        Vec3d eye = client.player.getEyePos();

        matrices.push();
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance()
                .begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        line(buffer, matrix, eye.x, eye.y, eye.z, target.x, target.y, target.z, r, g, b, 0.9f);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        matrices.pop();
    }

    private static void line(BufferBuilder buffer, Matrix4f matrix,
                             double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             float r, float g, float b, float a) {
        buffer.vertex(matrix, (float) x1, (float) y1, (float) z1).color(r, g, b, a);
        buffer.vertex(matrix, (float) x2, (float) y2, (float) z2).color(r, g, b, a);
    }
}
