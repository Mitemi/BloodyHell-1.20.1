package net.agusdropout.bloodyhell.block.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class BaseFireBlockEntity extends BlockEntity {
    @Nullable
    private UUID ownerUUID;

    public BaseFireBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public void setOwner(@Nullable LivingEntity owner) {
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
            this.setChanged();
        }
    }

    @Nullable
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public boolean isSafe(Entity entity) {
        if (ownerUUID == null) return false;


        if (entity.getUUID().equals(ownerUUID)) return true;


        if (this.level instanceof ServerLevel serverLevel) {
            Entity owner = serverLevel.getEntity(ownerUUID);
            if (owner != null) {
                return entity.isAlliedTo(owner) || owner.isAlliedTo(entity);
            }
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }
    }
}
