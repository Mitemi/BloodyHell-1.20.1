package net.agusdropout.bloodyhell.event.handlers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT)
public class TentacleOverlayHandler {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.VIGNETTE.type()) return;

        float intensity = LanternAmbienceHandler.getCurrentIntensity();
        if (intensity <= 0.01f) return;

        Minecraft mc = Minecraft.getInstance();

        int width = event.getGuiGraphics().guiWidth();
        int height = event.getGuiGraphics().guiHeight();
        PoseStack poseStack = event.getGuiGraphics().pose();

        var shader = ModShaders.FRENZY_TENTACLES;
        if (shader == null) return;


        if (shader.getUniform("Intensity") != null) {
            shader.getUniform("Intensity").set(intensity);
        }
        if (shader.getUniform("Time") != null) {
            shader.getUniform("Time").set((mc.level.getGameTime() + event.getPartialTick()) / 20.0f);
        }
        if (shader.getUniform("Resolution") != null) {
            shader.getUniform("Resolution").set((float) mc.getWindow().getScreenWidth(), (float) mc.getWindow().getScreenHeight());
        }


        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(() -> shader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        Matrix4f matrix4f = poseStack.last().pose();

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        bufferbuilder.vertex(matrix4f, 0, (float)height, 0).uv(0, 1).color(1f, 1f, 1f, 1f).endVertex();
        bufferbuilder.vertex(matrix4f, (float)width, (float)height, 0).uv(1, 1).color(1f, 1f, 1f, 1f).endVertex();
        bufferbuilder.vertex(matrix4f, (float)width, 0, 0).uv(1, 0).color(1f, 1f, 1f, 1f).endVertex();
        bufferbuilder.vertex(matrix4f, 0, 0, 0).uv(0, 0).color(1f, 1f, 1f, 1f).endVertex();

        tesselator.end();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }
}