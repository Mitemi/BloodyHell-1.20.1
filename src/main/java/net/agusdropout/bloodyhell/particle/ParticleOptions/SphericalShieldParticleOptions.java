package net.agusdropout.bloodyhell.particle.ParticleOptions;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;

public class SphericalShieldParticleOptions implements ParticleOptions {

    public static final Codec<SphericalShieldParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("r").forGetter(SphericalShieldParticleOptions::getR),
            Codec.FLOAT.fieldOf("g").forGetter(SphericalShieldParticleOptions::getG),
            Codec.FLOAT.fieldOf("b").forGetter(SphericalShieldParticleOptions::getB),
            Codec.FLOAT.fieldOf("radius").forGetter(SphericalShieldParticleOptions::getRadius),
            Codec.INT.fieldOf("lifetime").forGetter(SphericalShieldParticleOptions::getLifetime)
    ).apply(instance, SphericalShieldParticleOptions::new));

    public static final ParticleOptions.Deserializer<SphericalShieldParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public SphericalShieldParticleOptions fromCommand(ParticleType<SphericalShieldParticleOptions> particleType, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float radius = reader.readFloat();
            reader.expect(' ');
            int lifetime = reader.readInt();
            return new SphericalShieldParticleOptions(r, g, b, radius, lifetime);
        }

        @Override
        public SphericalShieldParticleOptions fromNetwork(ParticleType<SphericalShieldParticleOptions> particleType, FriendlyByteBuf buffer) {
            return new SphericalShieldParticleOptions(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt());
        }
    };

    private final float r;
    private final float g;
    private final float b;
    private final float radius;
    private final int lifetime;

    public SphericalShieldParticleOptions(float r, float g, float b, float radius, int lifetime) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.radius = radius;
        this.lifetime = lifetime;
    }

    public float getR() { return this.r; }
    public float getG() { return this.g; }
    public float getB() { return this.b; }
    public float getRadius() { return this.radius; }
    public int getLifetime() { return this.lifetime; }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.SPHERICAL_SHIELD_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.r);
        buffer.writeFloat(this.g);
        buffer.writeFloat(this.b);
        buffer.writeFloat(this.radius);
        buffer.writeInt(this.lifetime);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %d",
                ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()),
                this.r, this.g, this.b, this.radius, this.lifetime);
    }
}