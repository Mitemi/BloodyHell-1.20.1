package net.agusdropout.bloodyhell.particle.ParticleOptions;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

import java.util.Locale;

public class NoiseSphereParticleOptions implements ParticleOptions {

    private final Vector3f color;
    private final float initialSize;
    private final int lifeTicks;

    public static final Codec<NoiseSphereParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(o -> o.color),
            Codec.FLOAT.fieldOf("initial_size").forGetter(p -> p.initialSize),
            Codec.INT.fieldOf("life_ticks").forGetter(p -> p.lifeTicks)
    ).apply(instance, NoiseSphereParticleOptions::new));

    public static final ParticleOptions.Deserializer<NoiseSphereParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public NoiseSphereParticleOptions fromCommand(ParticleType<NoiseSphereParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float size = reader.readFloat();
            reader.expect(' ');
            int life = reader.readInt();
            return new NoiseSphereParticleOptions(new Vector3f(r, g, b), size, life);
        }

        @Override
        public NoiseSphereParticleOptions fromNetwork(ParticleType<NoiseSphereParticleOptions> type, FriendlyByteBuf buf) {
            return new NoiseSphereParticleOptions(new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()), buf.readFloat(), buf.readInt());
        }
    };

    public NoiseSphereParticleOptions(Vector3f color, float initialSize, int lifeTicks) {
        this.color = color;
        this.initialSize = initialSize;
        this.lifeTicks = lifeTicks;
    }

    public Vector3f getColor() {
        return color;
    }

    public float getInitialSize() {
        return initialSize;
    }

    public int getLifeTicks() {
        return lifeTicks;
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.NOISE_SPHERE_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.color.x());
        buf.writeFloat(this.color.y());
        buf.writeFloat(this.color.z());
        buf.writeFloat(this.initialSize);
        buf.writeInt(this.lifeTicks);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %d",
                ModParticles.NOISE_SPHERE_PARTICLE.getId(),
                this.color.x(), this.color.y(), this.color.z(),
                this.initialSize, this.lifeTicks);
    }
}