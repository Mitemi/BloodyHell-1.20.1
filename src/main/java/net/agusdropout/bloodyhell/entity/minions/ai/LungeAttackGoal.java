package net.agusdropout.bloodyhell.entity.minions.ai;

import net.agusdropout.bloodyhell.entity.minions.custom.BastionOfTheUnknownEntity;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class LungeAttackGoal extends Goal {
    private final BastionOfTheUnknownEntity entity;
    private LivingEntity target;
    private final int lungeDuration;
    private final int cooldownDuration;
    private int currentCooldown;
    private int lungeTicks;

    public LungeAttackGoal(BastionOfTheUnknownEntity entity, int lungeDuration, int cooldownDuration) {
        this.entity = entity;
        this.lungeDuration = lungeDuration;
        this.cooldownDuration = cooldownDuration;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.currentCooldown > 0) {
            this.currentCooldown--;
            return false;
        }
        this.target = this.entity.getTarget();
        if (this.target == null || !this.target.isAlive()) {
            return false;
        }
        if(entity.getOnPos().getY() != target.getOnPos().getY()){
            return false;
        }

        double distance = this.entity.distanceToSqr(this.target);
        return distance >= 5.0D && distance <= 100.0D ;
    }

    @Override
    public boolean canContinueToUse() {
        return this.lungeTicks < this.lungeDuration && this.target != null && this.target.isAlive();
    }

    @Override
    public void start() {
        this.lungeTicks = 0;
        this.entity.setLunging(true);
        this.entity.getNavigation().stop();
        this.entity.triggerAnim("action_controller", "lunge");


        Vec3 targetPos = this.target.position();
        Vec3 entityPos = this.entity.position();
        Vec3 direction = targetPos.subtract(entityPos).normalize();

        this.entity.setPos(this.entity.getX(), this.entity.getY(), this.entity.getZ());

        double lungeStrength = 1.9D;
        this.entity.setDeltaMovement(direction.x * lungeStrength, 0.00D, direction.z * lungeStrength);
    }

    @Override
    public void tick() {
        this.lungeTicks++;
        this.entity.lookAt(this.target, 30.0F, 30.0F);
        entity.level().playSound(null, entity.getOnPos(), ModSounds.SELIORA_CHARGE_ATTACK_SOUND.get(),
                SoundSource.HOSTILE, 1.5F, 0.9F + entity.level().random.nextFloat() * 0.2F);
        entity.level().playSound(null, entity.getOnPos(), SoundEvents.TRIDENT_RIPTIDE_3,
                SoundSource.HOSTILE, 1.0F, 1.0F);
        if (this.entity.getBoundingBox().inflate(0.5D).intersects(this.target.getBoundingBox())) {
            this.entity.doHurtTarget(this.target);
        }
    }

    @Override
    public void stop() {
        this.currentCooldown = this.cooldownDuration;
        this.entity.setLunging(false);
        this.entity.setDeltaMovement(0, this.entity.getDeltaMovement().y, 0);
        this.entity.triggerAnim("action_controller", "reposition");
    }
}