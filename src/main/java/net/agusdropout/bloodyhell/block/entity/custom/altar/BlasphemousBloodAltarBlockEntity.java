package net.agusdropout.bloodyhell.block.entity.custom.altar;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.AbstractAltarPedestalBlockEntity;
import net.agusdropout.bloodyhell.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

public class BlasphemousBloodAltarBlockEntity extends AbstractAltarPedestalBlockEntity implements GeoBlockEntity {

    public final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private int activeRitualTicks = 0;


    public BlasphemousBloodAltarBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.BLASPHEMOUS_BLOOD_ALTAR.get(), blockPos, blockState, 3);
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if(activeRitualTicks > 0) {
            activeRitualTicks--;
            float progress = (float) (MainBlasphemousBloodAltarBlockEntity.MAX_ACTIVE_RITUAL_TICKS - activeRitualTicks) / MainBlasphemousBloodAltarBlockEntity.MAX_ACTIVE_RITUAL_TICKS;

            float currentSpeed = 1.0f + (2.5f * progress);

            tAnimationState.getController().setAnimationSpeed(currentSpeed);
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("active", Animation.LoopType.LOOP));

        } else if (isSomethingInside()) {
            tAnimationState.getController().setAnimationSpeed(1.0f);
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("active", Animation.LoopType.LOOP));
        } else {
            tAnimationState.getController().setAnimationSpeed(1.0f);
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }



    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }

    @Override
    public float getFluidRadius() {
        return 0.24f;
    }

    @Override
    public float getFluidYOffset() {
        return 1.3f;
    }

    @Override
    public float getFluidHeight() {
        return 0.1f;
    }

    @Override
    public Fluid getFluidType() {
        return ModFluids.BLOOD_SOURCE.get();
    }

    @Override
    public int getMaxItemCount() {
        return 3;
    }

    public boolean isGeckoLib() {
        return true;
    }

    public void setActiveRitualTicks(){
        this.activeRitualTicks = MainBlasphemousBloodAltarBlockEntity.MAX_ACTIVE_RITUAL_TICKS;
    }
}