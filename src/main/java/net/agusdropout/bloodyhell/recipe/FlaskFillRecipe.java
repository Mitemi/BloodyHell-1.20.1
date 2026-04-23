package net.agusdropout.bloodyhell.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;


/* A dummy record to hold the data for the flask filling recipe.
This is used to pass the data from the recipe serializer to the recipe type. */

public record FlaskFillRecipe(FluidStack requiredFluid, ItemStack emptyFlask, ItemStack filledFlask) {
}