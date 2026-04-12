package net.agusdropout.bloodyhell.entity.unknown.custom;

import net.agusdropout.bloodyhell.entity.base.InsightEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public class EchoOfTheNamelessEntity extends PathfinderMob implements GeoEntity, InsightEntity {

    public static final float REPEALING_LAMP_RADIUS = 5.0F;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(EchoOfTheNamelessEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> ENERGY = SynchedEntityData.defineId(EchoOfTheNamelessEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(EchoOfTheNamelessEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Optional<UUID>> NEXT_LAMP_UUID = SynchedEntityData.defineId(EchoOfTheNamelessEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public static final int STATE_IDLE = 0;
    public static final int STATE_UNBURROWING = 1;
    public static final int STATE_BURROWING = 2;

    private static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ANIM_UNBURROWING = RawAnimation.begin().thenPlay("unburrowing");
    private static final RawAnimation ANIM_BURROWING = RawAnimation.begin().thenPlay("burrowing");

    private int stateTicks = 0;
    private int resonanceTick = -1;

    public EchoOfTheNamelessEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATE, STATE_UNBURROWING);
        this.entityData.define(ENERGY, 100.0F);
        this.entityData.define(OWNER_UUID, Optional.empty());
        this.entityData.define(NEXT_LAMP_UUID, Optional.empty());
    }

    public int getEntityState() {
        return this.entityData.get(STATE);
    }

    public void setEntityState(int state) {
        this.entityData.set(STATE, state);
        this.stateTicks = 0;
    }

    public float getEnergy() {
        return this.entityData.get(ENERGY);
    }

    public void setEnergy(float energy) {
        this.entityData.set(ENERGY, Math.max(0.0F, Math.min(100.0F, energy)));
    }

    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    public void setOwnerUUID(UUID uuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    public UUID getNextLampUUID() {
        return this.entityData.get(NEXT_LAMP_UUID).orElse(null);
    }

    public void setNextLampUUID(UUID uuid) {
        this.entityData.set(NEXT_LAMP_UUID, Optional.ofNullable(uuid));
    }

    public boolean isFullyCharged() {
        return this.getEnergy() >= 95.0F;
    }

    public boolean holdsSufficientChargeForRepulsion() {
        return this.getEnergy() > 30.0F;
    }

    public void triggerResonance(int delay) {
        this.resonanceTick = delay;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    public void push(double x, double y, double z) {
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        int currentState = this.getEntityState();

        if (currentState == STATE_UNBURROWING || currentState == STATE_BURROWING) {
            BlockPos posBelow = this.blockPosition().below();
            BlockState stateBelow = this.level().getBlockState(posBelow);

            if (this.level().isClientSide()) {
                if (!stateBelow.isAir()) {
                    for (int i = 0; i < 4; i++) {
                        double offsetX = (this.random.nextDouble() - 0.5D) * 0.8D;
                        double offsetZ = (this.random.nextDouble() - 0.5D) * 0.8D;
                        this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, stateBelow),
                                this.getX() + offsetX, this.getY() + 0.15D, this.getZ() + offsetZ,
                                0.0D, 0.15D, 0.0D);
                    }
                }
            } else {
                if (this.tickCount % 5 == 0 && !stateBelow.isAir()) {
                    SoundType soundType = stateBelow.getSoundType(this.level(), posBelow, this);
                    this.playSound(soundType.getBreakSound(), soundType.getVolume() * 0.5F, soundType.getPitch() * 0.8F);
                }
            }

            this.stateTicks++;
            if (this.stateTicks >= 60 && !this.level().isClientSide()) {
                if (currentState == STATE_UNBURROWING) {
                    this.setEntityState(STATE_IDLE);
                } else {
                    this.discard();
                }
            }
        }

        if (!this.level().isClientSide()) {
            if (currentState == STATE_IDLE) {
                this.handleSafeZoneLogic();
            }

            if (this.resonanceTick >= 0) {
                if (this.resonanceTick == 0) {
                    this.executeResonance();
                }
                this.resonanceTick--;
            }
        }
    }

    private void executeResonance() {
        ServerLevel serverLevel = (ServerLevel) this.level();


        serverLevel.sendParticles(ParticleTypes.FIREWORK, this.getX(), this.getY() + 1.0D, this.getZ(), 15, 0.2D, 0.2D, 0.2D, 0.05D);
        this.playSound(net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME, 1.0F, 1.2F);

        UUID nextId = this.getNextLampUUID();
        if (nextId != null) {
            Entity nextEntity = serverLevel.getEntity(nextId);

            if (nextEntity != null) {

                net.minecraft.world.phys.Vec3 start = this.position().add(0, 1.0D, 0);
                net.minecraft.world.phys.Vec3 end = nextEntity.position().add(0, 1.0D, 0);
                net.minecraft.world.phys.Vec3 direction = end.subtract(start).normalize();
                double distance = start.distanceTo(end);

                for (double d = 0; d < distance; d += 0.5) {
                    net.minecraft.world.phys.Vec3 point = start.add(direction.scale(d));
                    serverLevel.sendParticles(ParticleTypes.END_ROD, point.x, point.y, point.z, 1, 0, 0, 0, 0.01);
                }


                if (nextEntity instanceof EchoOfTheNamelessEntity nextLamp) {
                    nextLamp.triggerResonance(10);
                } else if (nextEntity instanceof net.agusdropout.bloodyhell.entity.effects.NamelessTrialRiftEntity rift) {
                    serverLevel.sendParticles(ParticleTypes.FIREWORK, rift.getX(), rift.getY() + 1.0D, rift.getZ(), 30, 0.5D, 0.5D, 0.5D, 0.1D);
                    rift.playSound(net.minecraft.sounds.SoundEvents.AMETHYST_CLUSTER_BREAK, 1.5F, 1.0F);
                }
            }
        }
    }

    private void handleSafeZoneLogic() {
        float currentEnergy = this.getEnergy();

        if (currentEnergy > 0.0F) {
            java.util.List<net.minecraft.world.entity.player.Player> nearbyPlayers = this.level().getEntitiesOfClass(
                    net.minecraft.world.entity.player.Player.class,
                    this.getBoundingBox().inflate(5.0D)
            );

            if (!nearbyPlayers.isEmpty()) {
                this.setEnergy(currentEnergy - 0.4F);

                if (this.tickCount % 5 == 0) {
                    for (net.minecraft.world.entity.player.Player player : nearbyPlayers) {
                        net.minecraft.world.effect.MobEffectInstance frenzy = player.getEffect(net.agusdropout.bloodyhell.effect.ModEffects.FRENZY.get());

                        if (frenzy != null) {
                            int currentAmp = frenzy.getAmplifier();
                            int newAmp = currentAmp - 30;

                            if (newAmp < 0) {
                                player.removeEffect(net.agusdropout.bloodyhell.effect.ModEffects.FRENZY.get());
                            } else {
                                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                        net.agusdropout.bloodyhell.effect.ModEffects.FRENZY.get(),
                                        60,
                                        newAmp,
                                        false,
                                        false,
                                        true
                                ));
                            }
                        }
                    }
                }
            }
        } else {
            if (this.getEntityState() != STATE_BURROWING) {
                this.setEntityState(STATE_BURROWING);
                this.playSound(net.minecraft.sounds.SoundEvents.FIRE_EXTINGUISH, 1.0F, 0.5F);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("LampState", this.getEntityState());
        tag.putFloat("LampEnergy", this.getEnergy());
        if (this.getOwnerUUID() != null) {
            tag.putUUID("OwnerUUID", this.getOwnerUUID());
        }
        if (this.getNextLampUUID() != null) {
            tag.putUUID("NextLampUUID", this.getNextLampUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("LampState")) {
            this.setEntityState(tag.getInt("LampState"));
        }
        if (tag.contains("LampEnergy")) {
            this.setEnergy(tag.getFloat("LampEnergy"));
        }
        if (tag.hasUUID("OwnerUUID")) {
            this.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        if (tag.hasUUID("NextLampUUID")) {
            this.setNextLampUUID(tag.getUUID("NextLampUUID"));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            int state = this.getEntityState();
            if (state == STATE_UNBURROWING) {
                return event.setAndContinue(ANIM_UNBURROWING);
            } else if (state == STATE_BURROWING) {
                return event.setAndContinue(ANIM_BURROWING);
            } else {
                return event.setAndContinue(ANIM_IDLE);
            }
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public float getMinimumInsight() {
        return 50;
    }
}