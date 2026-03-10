package net.agusdropout.bloodyhell.block.entity.custom.altar;

import net.agusdropout.bloodyhell.block.entity.ModBlockEntities;
import net.agusdropout.bloodyhell.block.entity.base.AbstractMainAltarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MainBloodAltarBlockEntity extends AbstractMainAltarBlockEntity {

    private ItemStack resultItem = ItemStack.EMPTY;
    public float lastParticleTime = 0.0f;

    public MainBloodAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MAIN_BLOOD_ALTAR_BE.get(), pos, state);
    }

    public ItemStack getResultItem() {
        return resultItem;
    }

    public void setResultItem(ItemStack resultItem) {
        this.resultItem = resultItem;
        setChanged();
    }

    public void clearResultItem() {
        this.resultItem = ItemStack.EMPTY;
        setChanged();
    }

    public boolean hasResultItem() {
        return !this.resultItem.isEmpty();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("ResultItem")) {
            this.resultItem = ItemStack.of(nbt.getCompound("ResultItem"));
        } else {
            this.resultItem = ItemStack.EMPTY;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (!this.resultItem.isEmpty()) {
            nbt.put("ResultItem", this.resultItem.save(new CompoundTag()));
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }
}