package net.agusdropout.bloodyhell.particle.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.particle.ParticleOptions.NoiseSphereParticleOptions;
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
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class NoiseSphereParticle extends Particle {

    private static final ResourceLocation OUTER_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/particle/sphere_noise_alt.png");
    private static final ResourceLocation INNER_TEXTURE = new ResourceLocation(BloodyHell.MODID, "textures/particle/sphere_noise_abs.png");

    private static final float GROW_FACTOR = 3.0f;

    private final Vector3f color;
    private final float maxRadius;

    protected NoiseSphereParticle(ClientLevel level, double x, double y, double z, Vector3f color, float initialSize, int lifeTicks) {
        super(level, x, y, z);
        this.color = color;
        this.maxRadius = initialSize;
        this.lifetime = lifeTicks;

        this.xo = x;
        this.yo = y;
        this.zo = z;

        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public void render(VertexConsumer ignored, Camera camera, float partialTick) {
        float time = this.age + partialTick;
        float lifeRatio = time / (float) this.lifetime;


        float expansionFactor = 1.0f - (float) Math.pow(1.0f - lifeRatio, GROW_FACTOR);
        float currentRadius = this.maxRadius * expansionFactor;


        float alpha = 1.0f - (float) Math.pow(lifeRatio, 2);

        alpha = Mth.clamp(1.0f - lifeRatio, 0.0f, 1.0f);

        float uOffset = time * 0.008F;
        float vOffset = time * 0.007F;


        Vec3 camPos = camera.getPosition();
        float px = (float) (Mth.lerp(partialTick, this.xo, this.x) - camPos.x());
        float py = (float) (Mth.lerp(partialTick, this.yo, this.y) - camPos.y());
        float pz = (float) (Mth.lerp(partialTick, this.zo, this.z) - camPos.z());

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();
        PoseStack poseStack = new PoseStack();
        poseStack.translate(px, py, pz);


        int r = (int) (this.color.x() * 255);
        int g = (int) (this.color.y() * 255);
        int b = (int) (this.color.z() * 255);
        int a = (int) (alpha * 255);

        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);

        // Outer Sphere
        RenderSystem.setShaderTexture(0, OUTER_TEXTURE);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        poseStack.pushPose();
        RenderHelper.renderTexturedSphereNoLight(buffer, poseStack, currentRadius, 24, 24, r, g, b, a, uOffset, vOffset);
        poseStack.popPose();
        tess.end();

        // Inner Sphere
        RenderSystem.setShaderTexture(0, INNER_TEXTURE);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        poseStack.pushPose();
        RenderHelper.renderTexturedSphereNoLight(buffer, poseStack, currentRadius * 0.95f, 24, 24, r, g, b, a, -uOffset, -vOffset);
        poseStack.popPose();
        tess.end();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Provider implements ParticleProvider<NoiseSphereParticleOptions> {
        @Override
        public Particle createParticle(NoiseSphereParticleOptions options, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new NoiseSphereParticle(level, x, y, z, options.getColor(), options.getInitialSize(), options.getLifeTicks());
        }
    }
}