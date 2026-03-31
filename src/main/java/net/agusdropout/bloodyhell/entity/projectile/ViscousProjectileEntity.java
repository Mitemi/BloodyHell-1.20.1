package net.agusdropout.bloodyhell.entity.projectile;

import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.entity.effects.EntityCameraShake;
import net.agusdropout.bloodyhell.entity.minions.base.AbstractMinionEntity;
import net.agusdropout.bloodyhell.entity.projectile.base.AbstractColoredProjectile;
import net.agusdropout.bloodyhell.entity.projectile.base.IAlliedProjectile;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.SyncVisceralEffectPacket;
import net.agusdropout.bloodyhell.particle.ParticleOptions.ChillFallingParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class ViscousProjectileEntity extends AbstractColoredProjectile implements IAlliedProjectile {

    public final AnimationState idleAnimationState = new AnimationState();
    private final float explosionRadius = 8.0f;

    public ViscousProjectileEntity(EntityType<? extends AbstractColoredProjectile> type, Level level) {
        super(type, level, false);
        setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.idleAnimationState.startIfStopped(this.tickCount);
        }

        Vec3 currentMovement = this.getDeltaMovement();
        this.setDeltaMovement(currentMovement.x, currentMovement.y - 0.042D, currentMovement.z);

    }

    @Override
    protected void handleClientEffects() {
        if (this.level().getRandom().nextFloat() < 0.5f) {
            Vector3f baseColor = this.getBaseColor();

            ChillFallingParticleOptions trailParticle = new ChillFallingParticleOptions(
                    baseColor,
                    0.05f,
                    20,
                    5
            );

            for( int i = 0; i < 10; i++) {

                double offsetX = (this.random.nextDouble() - 0.5) * 0.5;
                double offsetY = (this.random.nextDouble() - 0.5) * 0.5;
                double offsetZ = (this.random.nextDouble() - 0.5) * 0.5;

                ParticleHelper.spawn(
                        this.level(),
                        trailParticle,
                        this.getX() + offsetX,
                        this.getY() + 0.25 + offsetY,
                        this.getZ() + offsetZ,
                        0, 0, 0
                );
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            this.detonate();
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if(isAllied(result.getEntity())) return;

        if (!this.level().isClientSide) {
            this.detonate();
            this.discard();
        }
    }

    private void detonate() {
        Vec3 impactPos = this.position();
        Vector3f baseColor = this.getBaseColor();

        EntityCameraShake.cameraShake(this.level(), this.position(), explosionRadius, 0.5f, 15, 5);
        this.level().playSound(null, impactPos.x, impactPos.y, impactPos.z, SoundEvents.SLIME_BLOCK_BREAK, SoundSource.HOSTILE, 2.0F, 0.6F + this.random.nextFloat() * 0.2F);
        this.level().playSound(null, impactPos.x, impactPos.y, impactPos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 0.75F, 1.4F + this.random.nextFloat() * 0.2F);

        ChillFallingParticleOptions explosionParticle = new ChillFallingParticleOptions(
                baseColor,
                0.04f,
                80,
                0
        );

        ParticleHelper.spawnHemisphereExplosion(
                this.level(),
                explosionParticle,
                impactPos,
                80,
                this.explosionRadius,
                0.8
        );

        ParticleHelper.spawnCrownSplash(
                this.level(),
                explosionParticle,
                impactPos,
                80,
                1.5,
                0.5,
                0.4
        );

        AABB damageArea = new AABB(
                impactPos.x - this.explosionRadius,
                impactPos.y - this.explosionRadius,
                impactPos.z - this.explosionRadius,
                impactPos.x + this.explosionRadius,
                impactPos.y + this.explosionRadius,
                impactPos.z + this.explosionRadius
        );

        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, damageArea);

        for (LivingEntity target : targets) {
            if (this.distanceToSqr(target) <= (this.explosionRadius * this.explosionRadius)) {
                if(isAllied(target)) continue;
                if(this.getOwner() != null) {
                    target.hurt(this.getOwner().damageSources().mobProjectile((Entity) this, (LivingEntity) this.getOwner()), this.damage);
                } else {
                    target.hurt(this.damageSources().magic(), this.damage);
                }
                target.addEffect(new MobEffectInstance(ModEffects.VISCERAL_EFFECT.get(), 100, 1));
                ModMessages.sendToClients(new SyncVisceralEffectPacket(target.getId(), 100, 0));
                target.invulnerableTime = 0;
            }
        }
    }

    @Override
    public boolean isAllied(Entity target) {
        if(target instanceof AbstractMinionEntity minion){
            return this.getOwner() != null && minion.isAlliedTo(this.getOwner()) ;
        }
        if(target instanceof Player player){
            return this.getOwner() != null && player.getUUID().equals(this.getOwner().getUUID());
        }

        return false;
    }


}