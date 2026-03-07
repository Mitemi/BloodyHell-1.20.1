package net.agusdropout.bloodyhell.particle.ParticleOptions;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;

public class BlackHoleParticleOptions implements ParticleOptions {
    public static final Codec<BlackHoleParticleOptions> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.FLOAT.fieldOf("size").forGetter((opt) -> opt.size),
                    Codec.FLOAT.fieldOf("r").forGetter((opt) -> opt.r),
                    Codec.FLOAT.fieldOf("g").forGetter((opt) -> opt.g),
                    Codec.FLOAT.fieldOf("b").forGetter((opt) -> opt.b),
                    Codec.BOOL.fieldOf("is_dynamic").forGetter((opt) -> opt.isDynamic)
            ).apply(instance, BlackHoleParticleOptions::new)
    );

    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<BlackHoleParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public BlackHoleParticleOptions fromCommand(ParticleType<BlackHoleParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float size = reader.readFloat();
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            boolean isDynamic = reader.readBoolean();
            return new BlackHoleParticleOptions(size, r, g, b, isDynamic);
        }

        @Override
        public BlackHoleParticleOptions fromNetwork(ParticleType<BlackHoleParticleOptions> type, FriendlyByteBuf buf) {
            return new BlackHoleParticleOptions(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readBoolean());
        }
    };

    private final float size;
    private final float r, g, b;
    private final boolean isDynamic;

    public BlackHoleParticleOptions(float size, float r, float g, float b, boolean isDynamic) {
        this.size = size;
        this.r = r;
        this.g = g;
        this.b = b;
        this.isDynamic = isDynamic;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.size);
        buf.writeFloat(this.r);
        buf.writeFloat(this.g);
        buf.writeFloat(this.b);
        buf.writeBoolean(this.isDynamic);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %b", ModParticles.BLACK_HOLE_PARTICLE.getId(), this.size, this.r, this.g, this.b, this.isDynamic);
    }

    @Override
    public ParticleType<BlackHoleParticleOptions> getType() {
        return ModParticles.BLACK_HOLE_PARTICLE.get();
    }

    public float getSize() { return size; }
    public float getR() { return r; }
    public float getG() { return g; }
    public float getB() { return b; }
    public boolean isDynamic() { return isDynamic; }
}