package net.agusdropout.bloodyhell.block.custom.altar;

import net.agusdropout.bloodyhell.block.base.AbstractMainAltarBlock;
import net.agusdropout.bloodyhell.block.entity.custom.altar.MainBloodAltarBlockEntity;
import net.agusdropout.bloodyhell.item.ModItems;
import net.agusdropout.bloodyhell.recipe.BlasphemousBloodAltarRecipe;
import net.agusdropout.bloodyhell.recipe.BloodAltarRecipe;
import net.agusdropout.bloodyhell.util.VanillaPacketDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class MainBloodAltarBlock extends AbstractMainAltarBlock {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final VoxelShape LOWER_SHAPE = Block.box(0, 0, 0, 16, 32, 16);
    private static final VoxelShape UPPER_SHAPE = Block.box(0, -16, 0, 16, 16, 16);

    public MainBloodAltarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? LOWER_SHAPE : UPPER_SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new MainBloodAltarBlockEntity(pos, state);
        }
        return null;
    }

    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (blockState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos below = blockPos.below();
            return level.getBlockState(below).use(level, player, interactionHand, blockHitResult.withPosition(below));
        }

        if (!(level.getBlockEntity(blockPos) instanceof MainBloodAltarBlockEntity altar)) {
            return InteractionResult.PASS;
        }

        if (interactionHand == InteractionHand.MAIN_HAND) {
            ItemStack heldItem = player.getMainHandItem();

            // 1. ACTIVATION
            if (heldItem.is(ModItems.CORRUPTED_BLOOD_FLASK.get())) {
                if(!level.isClientSide()) {
                    altar.setActive(true);
                    if (!player.isCreative()) {
                        heldItem.shrink(1);
                        ItemStack emptyFlask = new ItemStack(ModItems.BLOOD_FLASK.get());
                        if (heldItem.isEmpty()) {
                            player.setItemInHand(InteractionHand.MAIN_HAND, emptyFlask);
                        } else if (!player.getInventory().add(emptyFlask)) {
                            player.drop(emptyFlask, false);
                        }
                    }
                    VanillaPacketDispatcher.dispatchTEToNearbyPlayers(altar);
                    level.setBlock(blockPos, blockState.setValue(ACTIVE, true), 3);
                    level.setBlock(blockPos.above(), level.getBlockState(blockPos.above()).setValue(ACTIVE, true), 3);
                    level.playSound(null, blockPos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());


                // 2. RITUAL CRAFTING
            } else if (altar.isActive() && isAltarSetupReady(level, blockPos, BloodAltarBlock.class)) {

                if (level.isClientSide()) return InteractionResult.SUCCESS;

                List<List<Item>> itemsFromAltars = getItemsFromAltars(level, blockPos);

                if (itemsFromAltars.size() != 4) return InteractionResult.FAIL;

                SimpleContainer inventory = new SimpleContainer(4);
                for (int i = 0; i < 4; i++) {
                    List<Item> pedestalItems = itemsFromAltars.get(i);
                    if (pedestalItems.isEmpty()) return InteractionResult.FAIL;
                    inventory.setItem(i, new ItemStack(pedestalItems.get(0)));
                }


                Optional<BloodAltarRecipe> recipe = level.getRecipeManager()
                        .getRecipeFor(BloodAltarRecipe.Type.INSTANCE, inventory, level);

                if (recipe.isPresent()) {
                    ItemStack result = recipe.get().getResultItem(level.registryAccess());

                    consumeItemsFromAltars(level, blockPos);

                    altar.setActive(false);
                    level.setBlock(blockPos, blockState.setValue(ACTIVE, false), 3);
                    level.setBlock(blockPos.above(), level.getBlockState(blockPos.above()).setValue(ACTIVE, false), 3);
                    VanillaPacketDispatcher.dispatchTEToNearbyPlayers(altar);

                    ItemEntity itemEntity = new ItemEntity(level, blockPos.getX() + 0.5, blockPos.getY() + 1.5, blockPos.getZ() + 0.5, result.copy());
                    itemEntity.setDeltaMovement(0, 0.1, 0);
                    level.addFreshEntity(itemEntity);

                    level.playSound(null, blockPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, blockPos.getX() + 0.5, blockPos.getY() + 1.5, blockPos.getZ() + 0.5, 15, 0.2, 0.2, 0.2, 0.1);
                    }

                    return InteractionResult.CONSUME;

                } else {
                    level.playSound(null, blockPos, SoundEvents.BASALT_BREAK, SoundSource.BLOCKS, 0.5F, 0.5F);
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.SMOKE, blockPos.getX() + 0.5, blockPos.getY() + 1.5, blockPos.getZ() + 0.5, 10, 0.2, 0.2, 0.2, 0.05);
                    }
                    player.sendSystemMessage(Component.literal("§cThe corrupted blood rejects these offerings..."));
                    return InteractionResult.FAIL;
                }
            }
        }
        return InteractionResult.PASS;
    }
}