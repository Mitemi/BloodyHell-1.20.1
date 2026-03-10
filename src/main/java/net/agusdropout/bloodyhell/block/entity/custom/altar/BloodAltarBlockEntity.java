package net.agusdropout.bloodyhell.block.entity.custom.altar;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.AbstractAltarPedestalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BloodAltarBlockEntity extends AbstractAltarPedestalBlockEntity {

    public BloodAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOOD_ALTAR_BE.get(), pos, state, 1);
    }
}