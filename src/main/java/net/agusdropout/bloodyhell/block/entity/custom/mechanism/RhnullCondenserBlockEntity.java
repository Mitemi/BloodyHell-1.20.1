package net.agusdropout.bloodyhell.block.entity.custom.mechanism;

import net.agusdropout.bloodyhell.block.base.AbstractCondenserBlock;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.AbstractCondenserBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

public class RhnullCondenserBlockEntity extends AbstractCondenserBlockEntity {

    public RhnullCondenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RHNULL_CONDENSER_BE.get(), pos, state, 10000);
    }

    @Override
    protected boolean isValidFluid(FluidStack stack) {
        // Validation logic for Rhnull fluid
        return true;
    }

    @Override
    protected void processRecipes() {
        // Recipe logic implementation
    }
}