package net.agusdropout.bloodyhell.block.entity.custom.mechanism;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.BaseGeckoBlockEntity;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnknownPortalBlockEntity extends BaseGeckoBlockEntity {

    public final FluidTank inputTank = new FluidTank(4000, fluid -> fluid.getFluid() == ModFluids.CORRUPTED_BLOOD_SOURCE.get()) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (!level.isClientSide()) level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    };

    // Note: Ensure Blasphemous Liquid exists in your ModFluids!
    public final FluidTank outputTank = new FluidTank(4000, fluid -> fluid.getFluid() == ModFluids.VISCOUS_BLASPHEMY_SOURCE.get()) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (!level.isClientSide()) level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    };


    private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> new IFluidHandler() {
        @Override
        public int getTanks() { return 2; }
        @Override
        public @NotNull FluidStack getFluidInTank(int tank) { return tank == 0 ? inputTank.getFluid() : outputTank.getFluid(); }
        @Override
        public int getTankCapacity(int tank) { return tank == 0 ? inputTank.getCapacity() : outputTank.getCapacity(); }
        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) { return tank == 0 ? inputTank.isFluidValid(stack) : outputTank.isFluidValid(stack); }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return inputTank.fill(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return outputTank.drain(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return outputTank.drain(maxDrain, action);
        }
    });

    public UnknownPortalBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.UNKNOWN_PORTAL_BE.get(), pos, blockState);
    }

    @Override
    public String getAssetPathName() {
        return "unknown_portal_block";
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("InputTank", inputTank.writeToNBT(new CompoundTag()));
        tag.put("OutputTank", outputTank.writeToNBT(new CompoundTag()));
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inputTank.readFromNBT(tag.getCompound("InputTank"));
        outputTank.readFromNBT(tag.getCompound("OutputTank"));
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());
    }
}