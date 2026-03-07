package net.agusdropout.bloodyhell.block.custom.mechanism;

import net.agusdropout.bloodyhell.block.base.IFlaskInteractableBlock;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.custom.mechanism.UnknownPortalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class UnknownPortalBlock extends BaseEntityBlock implements IFlaskInteractableBlock {

    public UnknownPortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult flaskResult = handleFlaskInteraction(state, level, pos, player, hand);
        if (flaskResult.consumesAction()) {
            return flaskResult;
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new UnknownPortalBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return createTickerHelper(type, ModBlockEntities.UNKNOWN_PORTAL_BE.get(), UnknownPortalBlockEntity::clientTick);
        }
        return createTickerHelper(type, ModBlockEntities.UNKNOWN_PORTAL_BE.get(), UnknownPortalBlockEntity::serverTick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof UnknownPortalBlockEntity portalBE) {
                portalBE.destroySummonedArms(level);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}