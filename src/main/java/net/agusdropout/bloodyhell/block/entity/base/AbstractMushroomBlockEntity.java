package net.agusdropout.bloodyhell.block.entity.base;



import net.agusdropout.bloodyhell.block.base.AbstractMushroomBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMushroomBlockEntity extends BlockEntity {

    protected final FluidTank tank;
    private final LazyOptional<IFluidHandler> lazyFluidHandler;
    protected int timer = 0;

    public AbstractMushroomBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int capacity) {
        super(type, pos, state);

        this.tank = new FluidTank(capacity) {
            @Override
            protected void onContentsChanged() {
                setChanged();
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                return isFluidSupported(stack);
            }
        };
        this.lazyFluidHandler = LazyOptional.of(() -> tank);
    }

    protected abstract boolean isFluidSupported(FluidStack stack);
    protected abstract int getFluidCostPerCycle();
    protected abstract int getTickCycleInterval();
    protected abstract void applyEffect(Level level, BlockPos pos);
    protected abstract void spawnServerParticles(ServerLevel level, BlockPos pos);

    public static void tick(Level level, BlockPos pos, BlockState state, AbstractMushroomBlockEntity entity) {
        if (level.isClientSide) return;

        boolean hasFluid = entity.tank.getFluidAmount() >= entity.getFluidCostPerCycle();
        boolean isActiveState = state.getValue(AbstractMushroomBlock.ACTIVE);

        if (hasFluid != isActiveState) {
            level.setBlock(pos, state.setValue(AbstractMushroomBlock.ACTIVE, hasFluid), 3);
            isActiveState = hasFluid;
        }

        if (!isActiveState) return;

        entity.timer++;

        if (entity.timer % entity.getTickCycleInterval() == 0) {
            if (entity.drainTank(entity.getFluidCostPerCycle())) {
                entity.applyEffect(level, pos);

                if (level instanceof ServerLevel serverLevel) {
                    entity.spawnServerParticles(serverLevel, pos);
                }
            }
        }
    }

    public boolean drainTank(int amount) {
        if (tank.getFluidAmount() >= amount) {
            tank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
            setChanged();
            return true;
        }
        return false;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) return lazyFluidHandler.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt = tank.writeToNBT(nbt);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        tank.readFromNBT(nbt);
    }
}