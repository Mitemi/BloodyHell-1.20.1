package net.agusdropout.bloodyhell.block.custom;
import net.agusdropout.bloodyhell.block.base.AbstractFireBlock;
import net.agusdropout.bloodyhell.block.entity.custom.BloodFireBlockEntity;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.SyncBloodFireEffectPacket;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class BloodFireBlock extends AbstractFireBlock {

    public BloodFireBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }


    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && !entity.fireImmune() && entity instanceof LivingEntity living) {

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BloodFireBlockEntity fireEntity) {
                if (fireEntity.isSafe(living)) {
                    return;
                }
            }


            living.hurt(level.damageSources().inFire(), 2.0F);
            MobEffectInstance currentEffect = living.getEffect(ModEffects.BLOOD_FIRE_EFFECT.get());

            if (currentEffect == null || currentEffect.getDuration() < 40) {
                living.addEffect(new MobEffectInstance(ModEffects.BLOOD_FIRE_EFFECT.get(), 500, 0));
                ModMessages.sendToPlayersTrackingEntity(new SyncBloodFireEffectPacket(living.getId(), 500, 0), living);
                if (living instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    ModMessages.sendToPlayer(new SyncBloodFireEffectPacket(living.getId(), 500, 0), serverPlayer);
                }
            }
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
            if (random.nextInt(10) == 0) {
                level.addParticle(ModParticles.SMALL_BLOOD_FLAME_PARTICLE.get(),
                        pos.getX() + random.nextDouble(), pos.getY() + 0.2D, pos.getZ() + random.nextDouble(),
                        0.0D, 0.02D, 0.0D);
            }

        } else {

            if (random.nextInt(24) == 0) {
                level.playLocalSound(x, y, z, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
            }



            for (int i = 0; i < 3; ++i) {
                level.addParticle(ModParticles.SMALL_BLOOD_FLAME_PARTICLE.get(),
                        pos.getX() + random.nextDouble(),
                        pos.getY() + 0.1D + random.nextDouble() * 0.3D,
                        pos.getZ() + random.nextDouble(),
                        0.0D, 0, 0.0D);
            }

            if (random.nextInt(5) == 0) {
                level.addParticle(ParticleTypes.LARGE_SMOKE, x, y + 0.5D, z, 0.0D, 0.05D, 0.0D);
            }
        }
    }


}