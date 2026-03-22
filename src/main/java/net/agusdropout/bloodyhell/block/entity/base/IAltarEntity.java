package net.agusdropout.bloodyhell.block.entity.base;

import net.minecraft.world.item.ItemStack;

public interface IAltarEntity {
    void drops();
    boolean isSpace();
    boolean isSomethingInside();
    boolean storeItem(ItemStack stack);
    ItemStack retrieveItem();
}