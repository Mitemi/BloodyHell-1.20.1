package net.agusdropout.bloodyhell.block.entity.custom.altar;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.AbstractAltarPedestalBlockEntity;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class BloodAltarBlockEntity extends AbstractAltarPedestalBlockEntity {

    public BloodAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOOD_ALTAR_BE.get(), pos, state, 1);
    }

    @Override
    public float getFluidRadius() {
        return 0.24f;
    }

    @Override
    public float getFluidYOffset() {
        return 1.11f;
    }

    @Override
    public float getFluidHeight() {
        return 0.01f;
    }

    @Override
    public Fluid getFluidType() {
        return ModFluids.CORRUPTED_BLOOD_SOURCE.get();
    }

    @Override
    public int getMaxItemCount() {
        return 1;
    }

    public boolean isGeckoLib() {
        return false;
    }
}