package net.agusdropout.bloodyhell.block.entity.custom.mushroom;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.AbstractMushroomBlockEntity;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.SyncVisceralEffectPacket;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Vector3f;

import java.util.List;

public class VoraciousMushroomBlockEntity extends AbstractMushroomBlockEntity {

    private static final int BLOOD_COST_PER_TICK_CYCLE = 5;
    private static final int EFFECT_RADIUS = 2;

    public VoraciousMushroomBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VORACIOUS_MUSHROOM_BE.get(), pos, state, 3000);
    }

    @Override
    protected boolean isFluidSupported(FluidStack stack) {
        return stack.getFluid() == ModFluids.BLOOD_SOURCE.get()
                || stack.getFluid() == ModFluids.BLOOD_FLOWING.get();
    }

    @Override
    protected int getFluidCostPerCycle() {
        return BLOOD_COST_PER_TICK_CYCLE;
    }

    @Override
    protected int getTickCycleInterval() {
        return 20;
    }

    @Override
    protected void applyEffect(Level level, BlockPos pos) {
        AABB area = new AABB(pos).inflate(EFFECT_RADIUS);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);

        for (LivingEntity entity : entities) {
            if (!entity.hasEffect(ModEffects.VISCERAL_EFFECT.get()) || entity.getEffect(ModEffects.VISCERAL_EFFECT.get()).getDuration() < 20) {
                entity.addEffect(new MobEffectInstance(ModEffects.VISCERAL_EFFECT.get(), 100, 0));

                if (level instanceof ServerLevel) {
                    ModMessages.sendToClients(new SyncVisceralEffectPacket(entity.getId(), 100, 0));
                }
            }
        }
    }

    @Override
    protected void spawnServerParticles(ServerLevel level, BlockPos pos) {
        ParticleOptions particle = new MagicParticleOptions(new Vector3f(1.0f, 0.9f, 0.2f), 1.0f, false, 40);

        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        for (int i = 0; i < 16; i++) {
            double angle = i * (Math.PI * 2) / 16;
            double speed = 0.15;
            double velX = Math.cos(angle) * speed;
            double velZ = Math.sin(angle) * speed;

            level.sendParticles(particle,
                    centerX, centerY, centerZ,
                    0,
                    velX, 0.0, velZ,
                    1.0);
        }
    }
}