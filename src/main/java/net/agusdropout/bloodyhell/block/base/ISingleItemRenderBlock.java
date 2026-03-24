package net.agusdropout.bloodyhell.block.base;

import net.minecraft.world.item.ItemStack;

public interface ISingleItemRenderBlock {
    ItemStack getRenderItemStack();
    String getItemBoneName();
}