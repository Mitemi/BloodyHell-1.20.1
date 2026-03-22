package net.agusdropout.bloodyhell.block.entity.base;

import net.minecraftforge.items.ItemStackHandler;

import javax.swing.plaf.basic.BasicComboBoxUI;

public interface IHoldingItemsAltar {

    ItemStackHandler getItemHandler();
    int getMaxItemCount();
}
