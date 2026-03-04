package net.agusdropout.bloodyhell.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ViceralParticle extends TextureSheetParticle {
    private static final float GRAVITY = 0.02F;
    private static final float INITIAL_SPEED = 0.1F;
    private static final float AIR_RESISTANCE = 0.98F;
    private static final float SPREAD = 0.05F;

    private float rotSpeed;
    private final float spinAcceleration;

    protected ViceralParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double dx, double dy, double dz) {
        super(level, x, y, z);

        this.rotSpeed = (float) Math.toRadians(this.random.nextBoolean() ? -30.0 : 30.0);
        this.spinAcceleration = (float) Math.toRadians(this.random.nextBoolean() ? -5.0 : 5.0);

        this.lifetime = 40 + this.random.nextInt(20);
        this.gravity = GRAVITY;

        float size = this.random.nextBoolean() ? 0.05F : 0.075F;
        this.quadSize = size;
        this.setSize(size, size);


        this.xd = dx + (this.random.nextFloat() - 0.5) * SPREAD;
        this.zd = dz + (this.random.nextFloat() - 0.5) * SPREAD;
        this.yd = dy + this.random.nextFloat() * INITIAL_SPEED;

        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.lifetime-- <= 0) {
            this.remove();
            return;
        }


        this.yd -= this.gravity;


        this.move(this.xd, this.yd, this.zd);


        this.xd *= AIR_RESISTANCE;
        this.yd *= AIR_RESISTANCE;
        this.zd *= AIR_RESISTANCE;

        this.oRoll = this.roll;
        this.roll += this.rotSpeed / 20.0F;


        if (this.onGround) {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new ViceralParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
