package net.agusdropout.bloodyhell.block.custom.mushroom;

import net.agusdropout.bloodyhell.block.base.AbstractMushroomBlock;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.custom.mushroom.CrimsonLureMushroomBlockEntity;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class CrimsonLureMushroomBlock extends AbstractMushroomBlock {

    public CrimsonLureMushroomBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void spawnClientParticles(Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
        double y = pos.getY() + 0.5 + random.nextDouble() * 0.4;
        double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4;

        Vector3f crimsonColor = new Vector3f(0.8f, 0.1f, 0.1f);
        MagicParticleOptions particleData = new MagicParticleOptions(crimsonColor, 1.0f, false, 40);

        level.addParticle(particleData, x, y, z, 0.0D, 0.05D, 0.0D);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrimsonLureMushroomBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return createTickerHelper(type, ModBlockEntities.CRIMSON_LURE_MUSHROOM_BE.get(),
                CrimsonLureMushroomBlockEntity::tick);
    }
}