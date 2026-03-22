package net.agusdropout.bloodyhell.block.entity.base;

import net.minecraft.world.level.material.Fluid;

public interface IChargableAltarEntity {
    boolean isMainCharged();
    float getFluidRadius();
    float getFluidYOffset();
    float getFluidHeight();
    Fluid getFluidType();
    boolean isGeckoLib();


}
