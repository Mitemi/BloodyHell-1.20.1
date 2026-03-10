package net.agusdropout.bloodyhell.recipe;

import com.google.gson.JsonObject;
import net.agusdropout.bloodyhell.BloodyHell;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class CondenserRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final Ingredient itemInput;
    private final FluidStack fluidInput;
    private final ItemStack output;

    public CondenserRecipe(ResourceLocation id, Ingredient itemInput, FluidStack fluidInput, ItemStack output) {
        this.id = id;
        this.itemInput = itemInput;
        this.fluidInput = fluidInput;
        this.output = output;
    }

    public FluidStack getFluidInput() {
        return fluidInput;
    }

    public Ingredient getItemInput() {
        return itemInput;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }
        return itemInput.test(pContainer.getItem(0));
    }

    public boolean matchesFluid(FluidStack tankFluid) {
        return tankFluid.getFluid() == this.fluidInput.getFluid() && tankFluid.getAmount() >= this.fluidInput.getAmount();
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<CondenserRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "condensing";
    }

    public static class Serializer implements RecipeSerializer<CondenserRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(BloodyHell.MODID, "condensing");

        @Override
        public CondenserRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient itemInput = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "item_input"));

            JsonObject fluidJson = GsonHelper.getAsJsonObject(pSerializedRecipe, "fluid_input");
            String fluidName = GsonHelper.getAsString(fluidJson, "fluid");
            int amount = GsonHelper.getAsInt(fluidJson, "amount");
            FluidStack fluidInput = new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName)), amount);

            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            return new CondenserRecipe(pRecipeId, itemInput, fluidInput, output);
        }

        @Override
        public @Nullable CondenserRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient itemInput = Ingredient.fromNetwork(pBuffer);
            FluidStack fluidInput = pBuffer.readFluidStack();
            ItemStack output = pBuffer.readItem();
            return new CondenserRecipe(pRecipeId, itemInput, fluidInput, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, CondenserRecipe pRecipe) {
            pRecipe.itemInput.toNetwork(pBuffer);
            pBuffer.writeFluidStack(pRecipe.fluidInput);
            pBuffer.writeItemStack(pRecipe.output, false);
        }
    }
}