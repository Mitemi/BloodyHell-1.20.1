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

public class MagicalRingParticleOptions implements ParticleOptions {

    public static final Codec<MagicalRingParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(o -> o.color),
            Codec.FLOAT.fieldOf("radius").forGetter(o -> o.radius),
            Codec.FLOAT.fieldOf("height").forGetter(o -> o.height)
    ).apply(instance, MagicalRingParticleOptions::new));

    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<MagicalRingParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public MagicalRingParticleOptions fromCommand(ParticleType<MagicalRingParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float radius = reader.readFloat();
            reader.expect(' ');
            float height = reader.readFloat();
            return new MagicalRingParticleOptions(new Vector3f(r, g, b), radius, height);
        }

        @Override
        public MagicalRingParticleOptions fromNetwork(ParticleType<MagicalRingParticleOptions> type, FriendlyByteBuf buf) {
            return new MagicalRingParticleOptions(buf.readVector3f(), buf.readFloat(), buf.readFloat());
        }
    };

    private final Vector3f color;
    private final float radius;
    private final float height;

    public MagicalRingParticleOptions(Vector3f color, float radius, float height) {
        this.color = color;
        this.radius = radius;
        this.height = height;
    }

    public Vector3f getColor() { return color; }
    public float getRadius() { return radius; }
    public float getHeight() { return height; }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.MAGICAL_RING_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeVector3f(this.color);
        buf.writeFloat(this.radius);
        buf.writeFloat(this.height);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f",
                net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                this.color.x(), this.color.y(), this.color.z(), this.radius, this.height);
    }
}