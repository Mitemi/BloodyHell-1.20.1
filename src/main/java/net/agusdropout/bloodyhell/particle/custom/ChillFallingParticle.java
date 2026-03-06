package net.agusdropout.bloodyhell.particle.custom;

import net.agusdropout.bloodyhell.particle.ParticleOptions.ChillFallingParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChillFallingParticle extends TextureSheetParticle {

    private final float targetSize;
    private final int growTime;
    private final double targetvx;
    private final double targetvy;
    private final double targetvz;

    protected ChillFallingParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, ChillFallingParticleOptions options) {
        super(level, x, y, z, vx, vy, vz);


        this.rCol = options.getColor().x();
        this.gCol = options.getColor().y();
        this.bCol = options.getColor().z();
        this.targetSize = options.getTargetSize();
        this.lifetime = options.getLifetime() + this.random.nextInt(40);
        this.growTime = options.getStayStillTicks();

        this.alpha = 0.9f;
        this.quadSize = 0.0f;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;

        this.targetvx = vx;
        this.targetvy = vy;
        this.targetvz = vz;
        this.gravity = 0.0f;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        if (this.growTime > 0) {
            if (this.age <= this.growTime) {
                float progress = (float) this.age / (float) this.growTime;
                this.quadSize = this.targetSize * Mth.sin(progress * (float)Math.PI / 2f);

                this.xd = 0;
                this.yd = 0;
                this.zd = 0;
            } else {
                this.quadSize = this.targetSize;
                this.yd -= 0.005D;
                this.xd = targetvx;
                this.zd = targetvz;
            }
        } else {
            this.quadSize = this.targetSize;
            this.yd -= 0.005D;
            this.xd = targetvx;
            this.zd = targetvz;
        }

        if (this.age > this.lifetime - 20) {
            this.alpha = 0.9f * ((float) (this.lifetime - this.age) / 20f);
        }

        this.move(this.xd, this.yd, this.zd);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float tint) {
        return 0xF000F0;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<ChillFallingParticleOptions> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(ChillFallingParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ChillFallingParticle particle = new ChillFallingParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, options);
            particle.pickSprite(this.sprite);
            return particle;
        }
    }
}