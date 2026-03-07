package net.agusdropout.bloodyhell.block.entity.base;

import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public interface IFluidFilterable {


    Fluid getFilter();
    void setFilter(Fluid fluid);


    default boolean isFluidAllowed(FluidStack stack) {
        if (getFilter() == null || getFilter() == Fluids.EMPTY) {
            return true;
        }
        return stack.getFluid().isSame(getFilter());
    }

    default void cycleFilter(Level level, BlockPos pos, Player player) {
        List<Fluid> allowedFluids = getAllowedFluids();
        Fluid current = getFilter();


        int index = allowedFluids.indexOf(current);
        if (index == -1) index = 0;

        int nextIndex = (index + 1) % allowedFluids.size();
        Fluid nextFluid = allowedFluids.get(nextIndex);

        setFilter(nextFluid);


        level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.BLOCKS, 0.5f, 1.2f);

        if (!level.isClientSide) {
            String fluidName = (nextFluid == Fluids.EMPTY) ? "None (Allow All)" : ForgeRegistries.FLUIDS.getKey(nextFluid).getPath();
            player.displayClientMessage(Component.literal("§6[Filter Set]: §f" + fluidName), true);
        }
    }

    default List<Fluid> getAllowedFluids() {
        return List.of(
                Fluids.EMPTY,
                Fluids.WATER,
                Fluids.LAVA,
                ModFluids.BLOOD_SOURCE.get(),
                ModFluids.CORRUPTED_BLOOD_SOURCE.get(),
                ModFluids.VISCOUS_BLASPHEMY_SOURCE.get(),
                ModFluids.VISCERAL_BLOOD_SOURCE.get()
        );
    }
}