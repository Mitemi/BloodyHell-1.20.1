package net.agusdropout.bloodyhell.block.entity.custom;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class FrenziedFireBlockEntity extends BlockEntity {

    private UUID ownerUUID;

    public FrenziedFireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FRENZIED_FIRE_BE.get(), pos, state);
    }

    public void setOwner(LivingEntity owner) {
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
            this.setChanged();
        }
    }

    public boolean isSafe(LivingEntity entity) {
        if (this.ownerUUID == null || entity == null) {
            return false;
        }
        return this.ownerUUID.equals(entity.getUUID());
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.ownerUUID != null) {
            tag.putUUID("OwnerUUID", this.ownerUUID);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID("OwnerUUID")) {
            this.ownerUUID = tag.getUUID("OwnerUUID");
        }
    }
}