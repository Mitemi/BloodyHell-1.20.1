package net.agusdropout.bloodyhell.particle.ParticleOptions;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.Vector3f;

import java.util.Locale;

public class TinyBloomParticleOptions implements ParticleOptions {

    public static final Codec<TinyBloomParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("r").forGetter(options -> options.color.x()),
            Codec.FLOAT.fieldOf("g").forGetter(options -> options.color.y()),
            Codec.FLOAT.fieldOf("b").forGetter(options -> options.color.z()),
            Codec.FLOAT.fieldOf("size").forGetter(TinyBloomParticleOptions::getSize)
    ).apply(instance, (r, g, b, size) -> new TinyBloomParticleOptions(new Vector3f(r, g, b), size)));

    public static final ParticleOptions.Deserializer<TinyBloomParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public TinyBloomParticleOptions fromCommand(ParticleType<TinyBloomParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float size = reader.readFloat();
            return new TinyBloomParticleOptions(new Vector3f(r, g, b), size);
        }

        @Override
        public TinyBloomParticleOptions fromNetwork(ParticleType<TinyBloomParticleOptions> type, FriendlyByteBuf buf) {
            return new TinyBloomParticleOptions(new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()), buf.readFloat());
        }
    };

    private final Vector3f color;
    private final float size;

    public TinyBloomParticleOptions(Vector3f color, float size) {
        this.color = color;
        this.size = size;
    }

    public Vector3f getColor() {
        return this.color;
    }

    public float getSize() {
        return this.size;
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.TINY_BLOOM.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.color.x());
        buf.writeFloat(this.color.y());
        buf.writeFloat(this.color.z());
        buf.writeFloat(this.size);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", this.getType().toString(), this.color.x(), this.color.y(), this.color.z(), this.size);
    }
}