package net.agusdropout.bloodyhell.block.base;

import net.agusdropout.bloodyhell.block.entity.base.AbstractCondenserBlockEntity;
import net.agusdropout.bloodyhell.datagen.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCondenserBlock extends Block implements EntityBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public AbstractCondenserBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(context)) {
            return this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            DoubleBlockHalf half = state.getValue(HALF);
            if (half == DoubleBlockHalf.UPPER) {
                BlockPos below = pos.below();
                BlockState stateBelow = level.getBlockState(below);
                if (stateBelow.is(this) && stateBelow.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    level.setBlock(below, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, below, Block.getId(stateBelow));
                }
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (facing.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
            return facingState.is(this) && facingState.getValue(HALF) != half ? state : Blocks.AIR.defaultBlockState();
        }
        return half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState stateBelow = level.getBlockState(pos.below());
            return stateBelow.is(this) && stateBelow.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
        return super.canSurvive(state, level, pos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos below = pos.below();
            return level.getBlockState(below).use(level, player, hand, hit.withPosition(below));
        }

        if (!(level.getBlockEntity(pos) instanceof AbstractCondenserBlockEntity condenser)) {
            return InteractionResult.PASS;
        }

        if (hand == InteractionHand.MAIN_HAND) {
            ItemStack heldItem = player.getMainHandItem();

            if (heldItem.is(ModTags.Items.GEM_FRAMES)) {
                if (condenser.getItemHandler().getStackInSlot(0).isEmpty()) {
                    if (!level.isClientSide()) {
                        ItemStack toInsert = heldItem.copy();
                        toInsert.setCount(1);
                        condenser.getItemHandler().setStackInSlot(0, toInsert);
                        if (!player.isCreative()) {
                            heldItem.shrink(1);
                        }
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            } else if (heldItem.isEmpty()) {
                ItemStack extracted = condenser.getItemHandler().getStackInSlot(0);
                if (!extracted.isEmpty()) {
                    if (!level.isClientSide()) {
                        player.getInventory().placeItemBackInInventory(extracted.copy());
                        condenser.getItemHandler().setStackInSlot(0, ItemStack.EMPTY);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }

        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return null;
        }
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof net.agusdropout.bloodyhell.block.entity.base.AbstractCondenserBlockEntity condenser) {
                condenser.tick();
            }
        };
    }
}