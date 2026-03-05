package net.agusdropout.bloodyhell.particle.custom;



import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicalRingParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.RenderHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class MagicalRingParticle extends Particle {

    private static final ResourceLocation AURORA_NOISE = new ResourceLocation(BloodyHell.MODID, "textures/particle/aurora_noise.png");
    private static final ResourceLocation AURORA_NOISE_ALT = new ResourceLocation(BloodyHell.MODID, "textures/particle/aurora_noise2.png");
    private static final boolean USE_TEXTURE = true;

    private final float radius;
    private final float ringHeight;

    protected MagicalRingParticle(ClientLevel level, double x, double y, double z, double maxLifeTime, MagicalRingParticleOptions options) {
        super(level, x, y, z);
        this.lifetime = (maxLifeTime == 0) ? 100 : (int) maxLifeTime;

        Vector3f color = options.getColor();
        this.rCol = color.x();
        this.gCol = color.y();
        this.bCol = color.z();
        this.alpha = 0.8f;

        this.radius = options.getRadius();
        this.ringHeight = options.getHeight();
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float lifeRatio = (float) this.age / (float) this.lifetime;
            if (lifeRatio > 0.7f) {
                this.alpha = 0.8f * (1.0f - ((lifeRatio - 0.7f) / 0.3f));
            }
        }
    }

    @Override
    public void render(VertexConsumer ignored, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        double px = Mth.lerp(partialTicks, xo, x) - camPos.x;
        double py = Mth.lerp(partialTicks, yo, y) - camPos.y;
        double pz = Mth.lerp(partialTicks, zo, z) - camPos.z;

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        if (USE_TEXTURE) {
            RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
            RenderSystem.setShaderTexture(0, AURORA_NOISE);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        } else {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        }

        PoseStack stack = new PoseStack();
        stack.translate(px, py, pz);

        float time = (this.age + partialTicks) / 10.0f;

        if (USE_TEXTURE) {
            RenderHelper.renderTexturedAuroraRing(buffer, stack.last().pose(),
                    this.radius, this.ringHeight, 200, time*0.5f,
                    this.rCol, this.gCol, this.bCol, this.alpha);

            tess.end();
            RenderSystem.setShaderTexture(0, AURORA_NOISE_ALT);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);

            RenderHelper.renderTexturedAuroraRing(buffer, stack.last().pose(),
                    this.radius, this.ringHeight, 200, -time,
                    this.rCol, this.gCol, this.bCol, this.alpha);
        } else {
            RenderHelper.renderAuroraRing(buffer, stack.last().pose(),
                    this.radius, this.ringHeight, 200, time,
                    this.rCol, this.gCol, this.bCol, this.alpha);
        }

        tess.end();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<MagicalRingParticleOptions> {
        @Nullable
        @Override
        public Particle createParticle(MagicalRingParticleOptions options, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
            return new MagicalRingParticle(level, x, y, z, vx, options);
        }
    }
}