package net.agusdropout.bloodyhell.block.custom.altar;

import net.agusdropout.bloodyhell.block.entity.custom.altar.BloodAltarBlockEntity;
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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BloodAltarBlock extends BaseEntityBlock {

    public static final BooleanProperty ITEMINSIDE = BooleanProperty.create("iteminside");

    public BloodAltarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ITEMINSIDE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ITEMINSIDE);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BloodAltarBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BloodAltarBlockEntity altar) {
                altar.drops();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof BloodAltarBlockEntity altar)) {
            return InteractionResult.PASS;
        }

        if (hand == InteractionHand.MAIN_HAND) {
            ItemStack heldItem = player.getMainHandItem();

            if (!heldItem.isEmpty()) {
                if (altar.isSpace()) {
                    ItemStack itemToStore = heldItem.copy();
                    itemToStore.setCount(1);
                    heldItem.shrink(1);
                    boolean result = altar.storeItem(itemToStore);
                    VanillaPacketDispatcher.dispatchTEToNearbyPlayers(altar);
                    if (result) {
                        level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                        level.setBlock(pos, state.setValue(ITEMINSIDE, true), 3);
                        return InteractionResult.sidedSuccess(level.isClientSide());
                    }
                }
            } else {
                if (altar.isSomethingInside()) {
                    ItemStack retrievedItem = altar.retrieveItem();
                    if (!retrievedItem.isEmpty()) {
                        player.getInventory().placeItemBackInInventory(retrievedItem);
                        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(altar);
                        level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                        if (!altar.isSomethingInside()) {
                            level.setBlock(pos, state.setValue(ITEMINSIDE, false), 3);
                        }
                        return InteractionResult.sidedSuccess(level.isClientSide());
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }
}