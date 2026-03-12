package net.agusdropout.bloodyhell.entity.effects;

import net.agusdropout.bloodyhell.entity.custom.UnknownLanternEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public class UnknownLanternRiftEntity extends BlackHoleEntity {

    private UUID lanternOwnerId;


    private static final EntityDataAccessor<Optional<UUID>> TARGET_PLAYER = SynchedEntityData.defineId(UnknownLanternRiftEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public UnknownLanternRiftEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.setMaxAge(6000);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TARGET_PLAYER, Optional.empty());
    }

    public void setLanternOwner(UUID uuid) {
        this.lanternOwnerId = uuid;
    }

    public UUID getLanternOwner() {
        return this.lanternOwnerId;
    }

    public void setTargetPlayer(UUID uuid) {
        this.entityData.set(TARGET_PLAYER, Optional.ofNullable(uuid));
    }

    public UUID getTargetPlayer() {
        return this.entityData.get(TARGET_PLAYER).orElse(null);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide() && this.tickCount >= 6000) {
            this.failRift();
            return;
        }
        if(this.tickCount % 50 == 0 && !this.level().isClientSide()) {
            Entity owner = this.level() instanceof ServerLevel serverLevel ? serverLevel.getEntity(this.getLanternOwner()) : null;
            if(owner == null) {
                this.failRift();
                return;
            }
        }


        super.tick();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("LanternOwner")) {
            this.lanternOwnerId = tag.getUUID("LanternOwner");
        }
        if (tag.hasUUID("TargetPlayer")) {
            this.setTargetPlayer(tag.getUUID("TargetPlayer"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.lanternOwnerId != null) {
            tag.putUUID("LanternOwner", this.lanternOwnerId);
        }
        if (this.getTargetPlayer() != null) {
            tag.putUUID("TargetPlayer", this.getTargetPlayer());
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        UUID targetId = this.getTargetPlayer();


        if (targetId != null && !targetId.equals(player.getUUID())) {
            return InteractionResult.FAIL;
        }

        if (!this.level().isClientSide()) {
            this.closeRift(player);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    private void closeRift(Player player) {
        this.playSound(SoundEvents.GLASS_BREAK, 1.0F, 0.5F);
        this.playSound(SoundEvents.BEACON_DEACTIVATE, 2.0F, 0.8F);

        if (this.lanternOwnerId != null && this.level() instanceof ServerLevel serverLevel) {
            Entity owner = serverLevel.getEntity(this.lanternOwnerId);

            if (owner instanceof UnknownLanternEntity lantern) {
                lantern.success(player);
            }
        }
        this.discard();
    }

    private void failRift() {
        if (this.lanternOwnerId != null && this.level() instanceof ServerLevel serverLevel) {
            Entity owner = serverLevel.getEntity(this.lanternOwnerId);

            if (owner instanceof UnknownLanternEntity lantern) {
                lantern.fail();
            }
        }
        this.discard();
    }
}