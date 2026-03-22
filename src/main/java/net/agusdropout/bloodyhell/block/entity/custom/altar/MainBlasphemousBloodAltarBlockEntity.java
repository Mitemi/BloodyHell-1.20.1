package net.agusdropout.bloodyhell.block.entity.custom.altar;

import net.agusdropout.bloodyhell.block.custom.altar.BlasphemousBloodAltarBlock;
import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.AbstractMainAltarBlockEntity;
import net.agusdropout.bloodyhell.event.handlers.RitualAmbienceHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import java.util.List;

public class MainBlasphemousBloodAltarBlockEntity extends AbstractMainAltarBlockEntity implements GeoBlockEntity {

    public final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private int activeRitualTicks = 0;
    public static final  int MAX_ACTIVE_RITUAL_TICKS = 500;
    List<BlockPos> ritualPedestalPositions = List.of(
            this.getBlockPos().north(4),
            this.getBlockPos().east(4),
            this.getBlockPos().south(4),
            this.getBlockPos().west(4)
    );



    public MainBlasphemousBloodAltarBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.MAIN_BLASPHEMOUS_BLOOD_ALTAR.get(), blockPos, blockState);
    }

    private <T extends GeoBlockEntity> PlayState predicate(AnimationState<T> tAnimationState) {
        if(activeRitualTicks > 0) {
            activeRitualTicks--;
            tAnimationState.getController().setAnimationSpeed(3.5f + (0.01f * (MAX_ACTIVE_RITUAL_TICKS - activeRitualTicks) / MAX_ACTIVE_RITUAL_TICKS));
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("active", Animation.LoopType.LOOP));

        } else if (isActive()) {
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
        controllers.add(new AnimationController<>(this, "controller", 20, this::predicate));
    }

    public void setActiveRitualTicks( ) {
        this.activeRitualTicks = MAX_ACTIVE_RITUAL_TICKS;
        for ( BlockPos pedestalPos : ritualPedestalPositions) {
            if (level.getBlockEntity(pedestalPos) instanceof BlasphemousBloodAltarBlockEntity bloodAltarBE) {
                bloodAltarBE.setActiveRitualTicks();
            }
        }
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 2) {
            if (this.level.isClientSide) {
                setActiveRitualTicks();
            }
            return true;
        }
        return super.triggerEvent(id, type);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }
}