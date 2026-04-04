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

public class FrenziedFlameParticleOptions implements ParticleOptions {

    public static final Codec<FrenziedFlameParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("r").forGetter(FrenziedFlameParticleOptions::getR),
            Codec.FLOAT.fieldOf("g").forGetter(FrenziedFlameParticleOptions::getG),
            Codec.FLOAT.fieldOf("b").forGetter(FrenziedFlameParticleOptions::getB),
            Codec.INT.fieldOf("lifetime").forGetter(FrenziedFlameParticleOptions::getLifetime)
    ).apply(instance, FrenziedFlameParticleOptions::new));

    public static final ParticleOptions.Deserializer<FrenziedFlameParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public FrenziedFlameParticleOptions fromCommand(ParticleType<FrenziedFlameParticleOptions> particleType, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            int lifetime = reader.readInt();
            return new FrenziedFlameParticleOptions(r, g, b, lifetime);
        }

        @Override
        public FrenziedFlameParticleOptions fromNetwork(ParticleType<FrenziedFlameParticleOptions> particleType, FriendlyByteBuf buffer) {
            return new FrenziedFlameParticleOptions(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt());
        }
    };

    private final float r;
    private final float g;
    private final float b;
    private final int lifetime;

    public FrenziedFlameParticleOptions(float r, float g, float b, int lifetime) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.lifetime = lifetime;
    }

    public float getR() { return this.r; }
    public float getG() { return this.g; }
    public float getB() { return this.b; }
    public int getLifetime() { return this.lifetime; }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.FRENZIED_FLAME_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.r);
        buffer.writeFloat(this.g);
        buffer.writeFloat(this.b);
        buffer.writeInt(this.lifetime);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d",
                ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()),
                this.r, this.g, this.b, this.lifetime);
    }
}