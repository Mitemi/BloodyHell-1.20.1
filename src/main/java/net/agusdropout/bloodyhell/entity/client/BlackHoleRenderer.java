package net.agusdropout.bloodyhell.entity.client;



import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;

import net.agusdropout.bloodyhell.entity.effects.BlackHoleEntity;
import net.agusdropout.bloodyhell.util.visuals.ModShaders;
import net.agusdropout.bloodyhell.util.visuals.RenderHelper;
import net.agusdropout.bloodyhell.util.visuals.ShaderUtils;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class BlackHoleRenderer extends EntityRenderer<BlackHoleEntity> {

    public BlackHoleRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BlackHoleEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float radius = entity.getRadius();
        float time = (entity.tickCount + partialTicks) / 20.0f;
        float alpha = 1.0f;

        float lifeRatio = (float) entity.tickCount / entity.getMaxAge();
        if (lifeRatio > 0.8f) {
            alpha = 1.0f - ((lifeRatio - 0.8f) / 0.2f);
        } else if (lifeRatio < 0.1f) {
            alpha = lifeRatio / 0.1f;
        }

        Matrix4f pose = poseStack.last().pose();
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        if (ShaderUtils.areShadersActive()) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            RenderHelper.renderDisk(buffer, pose, null, 0.0f, radius, 64, 0.0f,
                    new float[]{0.0f, 0.0f, 0.0f, alpha},
                    new float[]{0.0f, 0.0f, 0.0f, 0.0f},
                    packedLight);
            tess.end();
        } else {
            ShaderInstance shader = ModShaders.BLACK_HOLE_SHADER;
            if (shader != null) {
                float r = ((entity.getColor() >> 16) & 0xFF) / 255.0f;
                float g = ((entity.getColor() >> 8) & 0xFF) / 255.0f;
                float b = (entity.getColor() & 0xFF) / 255.0f;

                RenderSystem.setShader(() -> ModShaders.BLACK_HOLE_SHADER);

                if (shader.safeGetUniform("u_color") != null) {
                    shader.safeGetUniform("u_color").set(r, g, b);
                }



                RenderSystem.setShaderTexture(0, new ResourceLocation("minecraft", "textures/misc/white.png"));

                if (shader.safeGetUniform("u_time") != null) {
                    shader.safeGetUniform("u_time").set(time/3);
                }
                if (shader.safeGetUniform("u_alpha") != null) {
                    shader.safeGetUniform("u_alpha").set(alpha);
                }

                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

                /* Reverses winding order to align normal with the positive Y axis. */
                buffer.vertex(pose, -radius, 0.01f, -radius).uv(0.0f, 0.0f).endVertex();
                buffer.vertex(pose, radius, 0.01f, -radius).uv(1.0f, 0.0f).endVertex();
                buffer.vertex(pose, radius, 0.01f, radius).uv(1.0f, 1.0f).endVertex();
                buffer.vertex(pose, -radius, 0.01f, radius).uv(0.0f, 1.0f).endVertex();

                tess.end();
            }
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BlackHoleEntity entity) {
        return null;
    }
}