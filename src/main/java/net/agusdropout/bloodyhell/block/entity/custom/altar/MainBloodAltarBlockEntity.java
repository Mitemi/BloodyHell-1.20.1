package net.agusdropout.bloodyhell.block.entity.custom.altar;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.AbstractMainAltarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MainBloodAltarBlockEntity extends AbstractMainAltarBlockEntity {

    public MainBloodAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MAIN_BLOOD_ALTAR_BE.get(), pos, state);
    }
}