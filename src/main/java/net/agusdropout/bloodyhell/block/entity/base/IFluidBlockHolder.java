package net.agusdropout.bloodyhell.block.entity.base;

import net.minecraftforge.fluids.capability.templates.FluidTank;

public interface IFluidBlockHolder {

    FluidTank getInputTank();
    float getFluidHeight();
    float getFluidRadius();
    float getFluidHeightOffset();
}
