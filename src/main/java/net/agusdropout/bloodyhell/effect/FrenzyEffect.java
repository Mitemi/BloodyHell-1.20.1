package net.agusdropout.bloodyhell.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FrenzyEffect extends MobEffect {

    public FrenzyEffect() {
        super(MobEffectCategory.HARMFUL, 0x590000);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (amplifier >= 99 && !entity.level().isClientSide()) {
            entity.hurt(entity.damageSources().magic(), 2.0F);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return amplifier >= 99 && duration % 10 == 0;
    }
}