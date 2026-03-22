package net.agusdropout.bloodyhell.block.base;

import net.agusdropout.bloodyhell.block.entity.base.IAltarEntity;
import net.agusdropout.bloodyhell.util.VanillaPacketDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public abstract class AbstractAltarBlock extends BaseEntityBlock {
    public static final BooleanProperty ITEMINSIDE = BooleanProperty.create("iteminside");
    public static final BooleanProperty MAINCHARGED = BooleanProperty.create("maincharged");

    protected AbstractAltarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ITEMINSIDE, false).setValue(MAINCHARGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ITEMINSIDE)
                .add(MAINCHARGED);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof IAltarEntity altar) {
                altar.drops();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof IAltarEntity altar)) return InteractionResult.PASS;



        ItemStack heldItem = player.getMainHandItem();

        if (!heldItem.isEmpty()) {
            if (altar.isSpace()) {
                ItemStack itemToStore = heldItem.copy();
                itemToStore.setCount(1);
                if (altar.storeItem(itemToStore)) {
                    heldItem.shrink(1);
                    finalizeInteraction(level, pos, state, altar, true);
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }
        } else if (altar.isSomethingInside()) {
            ItemStack retrievedItem = altar.retrieveItem();
            if (!retrievedItem.isEmpty()) {
                player.getInventory().placeItemBackInInventory(retrievedItem);
                finalizeInteraction(level, pos, state, altar, altar.isSomethingInside());
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return InteractionResult.PASS;
    }

    private void finalizeInteraction(Level level, BlockPos pos, BlockState state, IAltarEntity altar, boolean isInside) {
        VanillaPacketDispatcher.dispatchTEToNearbyPlayers((BlockEntity) altar);
        level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        level.setBlock(pos, state.setValue(ITEMINSIDE, isInside), 3);
    }
}