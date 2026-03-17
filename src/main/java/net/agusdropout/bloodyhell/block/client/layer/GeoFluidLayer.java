package net.agusdropout.bloodyhell.block.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.agusdropout.bloodyhell.block.entity.base.BaseGeckoBlockEntity;
import net.agusdropout.bloodyhell.block.entity.base.IGeoFluidBlock;
import net.agusdropout.bloodyhell.block.entity.base.IFluidBlockHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtils;

public class GeoFluidLayer <T extends BaseGeckoBlockEntity & IFluidBlockHolder & IGeoFluidBlock> extends GeoRenderLayer<T> {

    public GeoFluidLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        GeoBone bloodBone = bakedModel.getBone(animatable.getFluidBoneName()).orElse(null);
        if (bloodBone == null) return;

        FluidStack fluidStack = animatable.getInputTank().getFluid();
        if (fluidStack.isEmpty()) return;

        float fillPercentage = (float) animatable.getInputTank().getFluidAmount() / animatable.getInputTank().getCapacity();

        IClientFluidTypeExtensions fluidProps = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation fluidStill = fluidProps.getStillTexture(fluidStack);
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(fluidStill);

        int color = fluidProps.getTintColor(fluidStack);
        float alpha = ((color >> 24) & 0xFF) / 255f;
        float red = ((color >> 16) & 0xFF) / 255f;
        float green = ((color >> 8) & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;

        poseStack.pushPose();


        RenderUtils.prepMatrixForBone(poseStack, bloodBone);

        VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();

        poseStack.translate(0,0.9,0);


        float radius = animatable.getFluidRadius();
        float maxHeight = animatable.getFluidHeight() / 16.0f;
        float yOffset = animatable.getFluidHeightOffset();

        float xMin = -radius;
        float xMax = radius;
        float zMin = -radius;
        float zMax = radius;


        float yMin = yOffset;
        float yMax = yOffset + (maxHeight * fillPercentage);

        // Top Face
        vertex(builder, matrix, sprite, xMin, yMax, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 1, 0, 0, 0);
        vertex(builder, matrix, sprite, xMin, yMax, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 1, 0, 0, 1);
        vertex(builder, matrix, sprite, xMax, yMax, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 1, 0, 1, 1);
        vertex(builder, matrix, sprite, xMax, yMax, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 1, 0, 1, 0);

        // Bottom Face
        vertex(builder, matrix, sprite, xMin, yMin, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, -1, 0, 0, 0);
        vertex(builder, matrix, sprite, xMin, yMin, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, -1, 0, 0, 1);
        vertex(builder, matrix, sprite, xMax, yMin, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, -1, 0, 1, 1);
        vertex(builder, matrix, sprite, xMax, yMin, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, -1, 0, 1, 0);

        // North Face
        vertex(builder, matrix, sprite, xMax, yMax, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, -1, 0, 0);
        vertex(builder, matrix, sprite, xMax, yMin, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, -1, 0, 1);
        vertex(builder, matrix, sprite, xMin, yMin, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, -1, 1, 1);
        vertex(builder, matrix, sprite, xMin, yMax, zMin, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, -1, 1, 0);

        // South Face
        vertex(builder, matrix, sprite, xMin, yMax, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, 1, 0, 0);
        vertex(builder, matrix, sprite, xMin, yMin, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, 1, 0, 1);
        vertex(builder, matrix, sprite, xMax, yMin, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, 1, 1, 1);
        vertex(builder, matrix, sprite, xMax, yMax, zMax, red, green, blue, alpha, packedLight, packedOverlay, 0, 0, 1, 1, 0);

        // West Face
        vertex(builder, matrix, sprite, xMin, yMax, zMin, red, green, blue, alpha, packedLight, packedOverlay, -1, 0, 0, 0, 0);
        vertex(builder, matrix, sprite, xMin, yMin, zMin, red, green, blue, alpha, packedLight, packedOverlay, -1, 0, 0, 0, 1);
        vertex(builder, matrix, sprite, xMin, yMin, zMax, red, green, blue, alpha, packedLight, packedOverlay, -1, 0, 0, 1, 1);
        vertex(builder, matrix, sprite, xMin, yMax, zMax, red, green, blue, alpha, packedLight, packedOverlay, -1, 0, 0, 1, 0);

        // East Face
        vertex(builder, matrix, sprite, xMax, yMax, zMax, red, green, blue, alpha, packedLight, packedOverlay, 1, 0, 0, 0, 0);
        vertex(builder, matrix, sprite, xMax, yMin, zMax, red, green, blue, alpha, packedLight, packedOverlay, 1, 0, 0, 0, 1);
        vertex(builder, matrix, sprite, xMax, yMin, zMin, red, green, blue, alpha, packedLight, packedOverlay, 1, 0, 0, 1, 1);
        vertex(builder, matrix, sprite, xMax, yMax, zMin, red, green, blue, alpha, packedLight, packedOverlay, 1, 0, 0, 1, 0);

        poseStack.popPose();
    }

    private void vertex(VertexConsumer builder, Matrix4f matrix, TextureAtlasSprite sprite,
                        float x, float y, float z,
                        float r, float g, float b, float a,
                        int light, int overlay,
                        float nx, float ny, float nz,
                        float uOffset, float vOffset) {

        float u = (uOffset > 0.5f) ? sprite.getU1() : sprite.getU0();
        float v = (vOffset > 0.5f) ? sprite.getV1() : sprite.getV0();

        builder.vertex(matrix, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(nx, ny, nz)
                .endVertex();
    }
}

