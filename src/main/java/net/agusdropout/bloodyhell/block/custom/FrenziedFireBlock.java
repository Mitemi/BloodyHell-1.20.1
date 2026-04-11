package net.agusdropout.bloodyhell.block.custom;

import net.agusdropout.bloodyhell.block.base.AbstractFireBlock;
import net.agusdropout.bloodyhell.block.entity.custom.FrenziedFireBlockEntity;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.particle.ParticleOptions.TinyBloomParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public class FrenziedFireBlock extends AbstractFireBlock {

    public FrenziedFireBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && !entity.fireImmune() && entity instanceof LivingEntity living) {

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FrenziedFireBlockEntity fireEntity) {
                if (fireEntity.isSafe(living)) {
                    return;
                }
            }

            living.hurt(level.damageSources().inFire(), 2.0F);

            MobEffectInstance currentEffect = living.getEffect(ModEffects.FRENZY.get());
            int currentAmplifier = currentEffect != null ? currentEffect.getAmplifier() : -1;

            int newAmplifier = Math.min(99, currentAmplifier + 10);

            living.addEffect(new MobEffectInstance(ModEffects.FRENZY.get(), 200, newAmplifier, false, false, true));
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;

        if (state.getValue(WATERLOGGED)) {
            if (random.nextInt(10) == 0) {
                level.playLocalSound(x, y, z, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
            for (int i = 0; i < 4; ++i) {
                level.addParticle(ParticleTypes.BUBBLE_COLUMN_UP,
                        pos.getX() + random.nextDouble(), pos.getY() + 0.5D, pos.getZ() + random.nextDouble(),
                        0.0D, 0.1D + random.nextDouble() * 0.1D, 0.0D);
            }
            if (random.nextInt(3) == 0) {
                level.addParticle(ParticleTypes.CLOUD,
                        pos.getX() + random.nextDouble(), pos.getY() + 0.8D, pos.getZ() + random.nextDouble(),
                        0.0D, 0.05D, 0.0D);
            }
        } else {
            if (random.nextInt(24) == 0) {
                level.playLocalSound(x, y, z, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
            }

            for (int i = 0; i < 3; ++i) {
                level.addParticle(ParticleTypes.FLAME,
                        pos.getX() + random.nextDouble(),
                        pos.getY() + 0.1D + random.nextDouble() * 0.3D,
                        pos.getZ() + random.nextDouble(),
                        0.0D, 0.01D, 0.0D);
            }

            if (random.nextInt(5) == 0) {
                level.addParticle(ParticleTypes.LARGE_SMOKE, x, y + 0.5D, z, 0.0D, 0.05D, 0.0D);
            }
        }



        for(int i = 0; i < 10; i++) {
            double offsetX = (random.nextDouble() - 0.5D) * 1D;
            double offsetY = (random.nextDouble() - 0.5D) * 1D;
            double offsetZ = (random.nextDouble() - 0.5D) * 1D;

            float size = 0.05F + random.nextFloat() * 0.06F;

            Vector3f colorStart = new Vector3f(1.0F, 0.9F, 0.1F);
            Vector3f colorMid = new Vector3f(1.0F, 0.5F, 0.0F);
            Vector3f colorEnd = new Vector3f(0.8F, 0.1F, 0.0F);

            Vector3f gradientColor = ParticleHelper.gradient3(random.nextFloat(), colorStart, colorMid, colorEnd);

            TinyBloomParticleOptions bloomParticle = new TinyBloomParticleOptions(gradientColor, size);

            level.addParticle(bloomParticle,
                    x + offsetX,
                    y + offsetY,
                    z + offsetZ,
                    0.0D, 0.025D, 0.0D);
        }
    }
}