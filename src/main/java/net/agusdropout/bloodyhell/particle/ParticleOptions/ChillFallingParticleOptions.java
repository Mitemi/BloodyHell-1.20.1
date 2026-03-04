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

public class ChillFallingParticleOptions implements ParticleOptions {

    public static final Codec<ChillFallingParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(o -> o.color),
            Codec.FLOAT.fieldOf("target_size").forGetter(o -> o.targetSize),
            Codec.INT.fieldOf("lifetime").forGetter(o -> o.lifetime),
            Codec.INT.fieldOf("stay_still_ticks").forGetter(o -> o.stayStillTicks)
    ).apply(instance, ChillFallingParticleOptions::new));

    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<ChillFallingParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public ChillFallingParticleOptions fromCommand(ParticleType<ChillFallingParticleOptions> type, StringReader reader) throws CommandSyntaxException {
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
            reader.expect(' ');
            int stillTicks = reader.readInt();
            return new ChillFallingParticleOptions(new Vector3f(r, g, b), size, life, stillTicks);
        }

        @Override
        public ChillFallingParticleOptions fromNetwork(ParticleType<ChillFallingParticleOptions> type, FriendlyByteBuf buf) {
            return new ChillFallingParticleOptions(buf.readVector3f(), buf.readFloat(), buf.readInt(), buf.readInt());
        }
    };

    private final Vector3f color;
    private final float targetSize;
    private final int lifetime;
    private final int stayStillTicks;

    public ChillFallingParticleOptions(Vector3f color, float targetSize, int lifetime, int stayStillTicks) {
        this.color = color;
        this.targetSize = targetSize;
        this.lifetime = lifetime;
        this.stayStillTicks = stayStillTicks;
    }

    public Vector3f getColor() { return color; }
    public float getTargetSize() { return targetSize; }
    public int getLifetime() { return lifetime; }
    public int getStayStillTicks() { return stayStillTicks; }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.CHILL_FALLING_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeVector3f(this.color);
        buf.writeFloat(this.targetSize);
        buf.writeInt(this.lifetime);
        buf.writeInt(this.stayStillTicks);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %d %d",
                net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                this.color.x(), this.color.y(), this.color.z(), this.targetSize, this.lifetime, this.stayStillTicks);
    }
}