package net.agusdropout.bloodyhell.block.base;


import net.agusdropout.bloodyhell.block.custom.altar.BloodAltarBlock;
import net.agusdropout.bloodyhell.particle.ParticleOptions.MagicParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMainAltarBlock extends BaseEntityBlock {

    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public AbstractMainAltarBlock(Properties properties) {
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

    public boolean isAltarSetupReady(Level level, BlockPos pos, Class<? extends Block> pillarClass) {
        BlockPos[] pillars = {pos.north(4), pos.east(4), pos.south(4), pos.west(4)};
        for(BlockPos p : pillars) {
            if (!pillarClass.isInstance(level.getBlockState(p).getBlock())) return false;
        }
        return true;
    }

    public List<List<Item>> getItemsFromAltars(Level level, BlockPos pos) {
        List<List<Item>> items = new ArrayList<>();
        BlockPos[] altarPositions = {pos.north(4), pos.east(4), pos.south(4), pos.west(4)};
        for (BlockPos altarPos : altarPositions) {
            if (level.getBlockEntity(altarPos) instanceof IAltarPedestal entity) {
                if (!entity.getItemsInside().isEmpty()) {
                    items.add(entity.getItemsInside());
                } else {
                    items.add(new ArrayList<>());
                }
            }
        }
        return items;
    }

    public void consumeItemsFromAltars(Level level, BlockPos pos) {
        BlockPos[] posArr = {pos.north(4), pos.east(4), pos.south(4), pos.west(4)};
        for(BlockPos p : posArr) {
            if (level.getBlockEntity(p) instanceof IAltarPedestal entity) {
                entity.clearItemsInside();
            }
        }
    }


    protected void handleVisualEffects(Level level, BlockPos blockPos) {
        if (!level.isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) level;
            BlockPos[] altarPositions = {blockPos.north(4), blockPos.east(4), blockPos.south(4), blockPos.west(4)};
            Vector3f colorStart = new Vector3f(0.4f, 0.0f, 0.0f);
            Vector3f colorEnd = new Vector3f(1.0f, 0.1f, 0.1f);
            for (BlockPos pPos : altarPositions) {
                BlockState pState = level.getBlockState(pPos);
                if (pState.hasProperty(BloodAltarBlock.MAINCHARGED)) {
                    level.setBlock(pPos, pState.setValue(BloodAltarBlock.MAINCHARGED, true), 3);
                }
                Vec3 startVec = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 1.5, blockPos.getZ() + 0.5);
                Vec3 endVec = new Vec3(pPos.getX() + 0.5, pPos.getY() + 0.8, pPos.getZ() + 0.5);
                int particleCount = 12;
                for (int i = 0; i < particleCount; i++) {
                    float ratio = (float) i / (particleCount - 1);
                    Vector3f currentColor = ParticleHelper.gradient3(ratio, colorStart, colorEnd, colorEnd);
                    MagicParticleOptions options = new MagicParticleOptions(currentColor, 0.6f, false, 40, true);
                    Vec3 spawnPos = startVec.lerp(endVec, ratio);
                    ParticleHelper.spawn(serverLevel, options, spawnPos.x, spawnPos.y, spawnPos.z, 0, 0.01, 0);
                }
            }
        }

    }

    protected void dischargeAltars(Level level, BlockPos blockPos) {
        if (!level.isClientSide()) {
            BlockPos[] altarPositions = {blockPos.north(4), blockPos.east(4), blockPos.south(4), blockPos.west(4)};
            for (BlockPos pPos : altarPositions) {
                BlockState pState = level.getBlockState(pPos);
                if (pState.hasProperty(BloodAltarBlock.MAINCHARGED)) {
                    level.setBlock(pPos, pState.setValue(BloodAltarBlock.MAINCHARGED, false), 3);
                }
            }
        }
    }


}