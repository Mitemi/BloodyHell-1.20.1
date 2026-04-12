package net.agusdropout.bloodyhell.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class DelusionGraspEffect extends MobEffect {

    public DelusionGraspEffect() {
        super(MobEffectCategory.HARMFUL, 0x1A1A1A);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "C316F6D6-0775-430A-B394-1FA3F3BFE238", -0.15D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {


        if (entity.level() instanceof ServerLevel serverLevel) {
            int particlesToSpawn = (amplifier + 1) * 2;

            double xOffset = (entity.getRandom().nextDouble() - 0.5D) * entity.getBbWidth() * 1.5;
            double yOffset = entity.getRandom().nextDouble() * entity.getBbHeight();
            double zOffset = (entity.getRandom().nextDouble() - 0.5D) * entity.getBbWidth() * 1.5;

            serverLevel.sendParticles(ParticleTypes.SQUID_INK,
                    entity.getX() + xOffset, entity.getY() + yOffset, entity.getZ() + zOffset,
                    particlesToSpawn, 0.1, 0.1, 0.1, 0.02);
        }


        if (amplifier >= 2) {

            entity.setDeltaMovement(0, -0.8D, 0);
            entity.hasImpulse = true;

            if (!entity.level().isClientSide()) {

                entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 0, false, false, false));

                if (entity.tickCount % 20 == 0) {
                    entity.hurt(entity.damageSources().magic(), 2.0F);
                    entity.playSound(net.minecraft.sounds.SoundEvents.PHANTOM_BITE, 1.0F, 0.5F);
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }


}