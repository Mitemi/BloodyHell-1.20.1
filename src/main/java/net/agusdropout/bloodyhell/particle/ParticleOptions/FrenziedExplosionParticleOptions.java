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

public class FrenziedExplosionParticleOptions implements ParticleOptions {

    public static final Codec<FrenziedExplosionParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("size").forGetter(FrenziedExplosionParticleOptions::getSize)
    ).apply(instance, FrenziedExplosionParticleOptions::new));

    public static final ParticleOptions.Deserializer<FrenziedExplosionParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public FrenziedExplosionParticleOptions fromCommand(ParticleType<FrenziedExplosionParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float size = reader.readFloat();
            return new FrenziedExplosionParticleOptions(size);
        }

        @Override
        public FrenziedExplosionParticleOptions fromNetwork(ParticleType<FrenziedExplosionParticleOptions> type, FriendlyByteBuf buf) {
            return new FrenziedExplosionParticleOptions(buf.readFloat());
        }
    };

    private final float size;

    public FrenziedExplosionParticleOptions(float size) {
        this.size = size;
    }

    public float getSize() {
        return this.size;
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.FRENZIED_EXPLOSION.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.size);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f", this.getType().toString(), this.size);
    }
}