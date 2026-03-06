package net.agusdropout.bloodyhell.block.entity.custom.mushroom;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.AbstractMushroomBlockEntity;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Vector3f;

import java.util.List;

public class CrimsonLureMushroomBlockEntity extends AbstractMushroomBlockEntity {

    private static final int BLOOD_COST_PER_TICK_CYCLE = 10;
    private static final double ATTRACTION_RADIUS = 16.0D;

    public CrimsonLureMushroomBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRIMSON_LURE_MUSHROOM_BE.get(), pos, state, 4000);
    }

    @Override
    protected boolean isFluidSupported(FluidStack stack) {
        return stack.getFluid() == ModFluids.BLOOD_SOURCE.get()
                || stack.getFluid() == ModFluids.BLOOD_FLOWING.get();
    }

    @Override
    protected int getFluidCostPerCycle() {
        return BLOOD_COST_PER_TICK_CYCLE;
    }

    @Override
    protected int getTickCycleInterval() {
        return 20;
    }

    @Override
    protected void applyEffect(Level level, BlockPos pos) {
        AABB searchArea = new AABB(pos).inflate(ATTRACTION_RADIUS);
        List<Monster> monsters = level.getEntitiesOfClass(Monster.class, searchArea);

        for (Monster monster : monsters) {
            if (monster.isAlive()) {
                monster.setTarget(null);
                monster.getNavigation().moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 1.2D);
            }
        }
    }

    @Override
    protected void spawnServerParticles(ServerLevel level, BlockPos pos) {
        Vector3f crimsonColor = new Vector3f(0.8f, 0.1f, 0.1f);
        ParticleOptions particle = new MagicParticleOptions(crimsonColor, 1.0f, false, 30);

        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        for (int i = 0; i < 8; i++) {
            double angle = i * (Math.PI * 2) / 8;
            double radius = 1.0;
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            level.sendParticles(particle,
                    centerX + offsetX, centerY, centerZ + offsetZ,
                    0,
                    -offsetX * 0.1, 0.05, -offsetZ * 0.1,
                    1.0);
        }
    }
}