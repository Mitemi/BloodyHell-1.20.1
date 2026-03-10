package net.agusdropout.bloodyhell.block.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.block.custom.altar.BloodAltarBlock;
import net.agusdropout.bloodyhell.block.entity.custom.altar.BloodAltarBlockEntity;
import net.agusdropout.bloodyhell.fluid.ModFluids; // Ensure this is the correct path
import net.agusdropout.bloodyhell.util.ClientTickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BloodAltarRenderer implements BlockEntityRenderer<BloodAltarBlockEntity> {

    // --- ADJUSTABLE VARIABLES ---
    private static final float FLUID_RADIUS = 0.24f;
    private static final float FLUID_Y_OFFSET = 1.11f;
    private static final float FLUID_HEIGHT = 0.01f;
    // ----------------------------

    public BloodAltarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BloodAltarBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        double time = ClientTickHandler.ticksInGame + partialTick;


        if (blockEntity.getBlockState().getValue(BloodAltarBlock.MAINCHARGED)) {
            renderCorruptedBlood(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        }


        ItemStack stack = blockEntity.getItemHandler().getStackInSlot(0);
        if (!stack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5D, 1.25D, 0.5D);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) (time % 360)));
            float levitationOffset = (float) Math.sin((time % 360) / 10F) * 0.05F;
            poseStack.translate(0F, levitationOffset, 0F);
            poseStack.scale(0.5F, 0.5F, 0.5F);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack, ItemDisplayContext.FIXED, packedLight, packedOverlay,
                    poseStack, bufferSource, blockEntity.getLevel(), 0
            );
            poseStack.popPose();
        }
    }

    private void renderCorruptedBlood(BloodAltarBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        IClientFluidTypeExtensions fluidProps = IClientFluidTypeExtensions.of(ModFluids.CORRUPTED_BLOOD_TYPE.get());
        ResourceLocation fluidStill = fluidProps.getStillTexture();
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);

        int color = fluidProps.getTintColor();
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        poseStack.pushPose();
        poseStack.translate(0.5D, FLUID_Y_OFFSET, 0.5D);

        VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        float xMin = -FLUID_RADIUS;
        float xMax = FLUID_RADIUS;
        float zMin = -FLUID_RADIUS;
        float zMax = FLUID_RADIUS;
        float yMin = 0;


        float yMax = FLUID_HEIGHT + ((float)Math.sin((ClientTickHandler.ticksInGame + partialTick) * 0.1f) * 0.02f);


        drawVertex(builder, matrix, normal, sprite, xMin, yMax, zMin, r, g, b, a, packedLight, packedOverlay, 0, 1, 0, 0, 0);
        drawVertex(builder, matrix, normal, sprite, xMin, yMax, zMax, r, g, b, a, packedLight, packedOverlay, 0, 1, 0, 0, 1);
        drawVertex(builder, matrix, normal, sprite, xMax, yMax, zMax, r, g, b, a, packedLight, packedOverlay, 0, 1, 0, 1, 1);
        drawVertex(builder, matrix, normal, sprite, xMax, yMax, zMin, r, g, b, a, packedLight, packedOverlay, 0, 1, 0, 1, 0);

        drawVertex(builder, matrix, normal, sprite, xMin, yMin, zMax, r, g, b, a, packedLight, packedOverlay, 0, -1, 0, 0, 0);
        drawVertex(builder, matrix, normal, sprite, xMin, yMin, zMin, r, g, b, a, packedLight, packedOverlay, 0, -1, 0, 0, 1);
        drawVertex(builder, matrix, normal, sprite, xMax, yMin, zMin, r, g, b, a, packedLight, packedOverlay, 0, -1, 0, 1, 1);
        drawVertex(builder, matrix, normal, sprite, xMax, yMin, zMax, r, g, b, a, packedLight, packedOverlay, 0, -1, 0, 1, 0);

        drawVertex(builder, matrix, normal, sprite, xMax, yMax, zMin, r, g, b, a, packedLight, packedOverlay, 0, 0, -1, 0, 0);
        drawVertex(builder, matrix, normal, sprite, xMax, yMin, zMin, r, g, b, a, packedLight, packedOverlay, 0, 0, -1, 0, 1);
        drawVertex(builder, matrix, normal, sprite, xMin, yMin, zMin, r, g, b, a, packedLight, packedOverlay, 0, 0, -1, 1, 1);
        drawVertex(builder, matrix, normal, sprite, xMin, yMax, zMin, r, g, b, a, packedLight, packedOverlay, 0, 0, -1, 1, 0);

        drawVertex(builder, matrix, normal, sprite, xMin, yMax, zMax, r, g, b, a, packedLight, packedOverlay, 0, 0, 1, 0, 0);
        drawVertex(builder, matrix, normal, sprite, xMin, yMin, zMax, r, g, b, a, packedLight, packedOverlay, 0, 0, 1, 0, 1);
        drawVertex(builder, matrix, normal, sprite, xMax, yMin, zMax, r, g, b, a, packedLight, packedOverlay, 0, 0, 1, 1, 1);
        drawVertex(builder, matrix, normal, sprite, xMax, yMax, zMax, r, g, b, a, packedLight, packedOverlay, 0, 0, 1, 1, 0);

        drawVertex(builder, matrix, normal, sprite, xMin, yMax, zMin, r, g, b, a, packedLight, packedOverlay, -1, 0, 0, 0, 0);
        drawVertex(builder, matrix, normal, sprite, xMin, yMin, zMin, r, g, b, a, packedLight, packedOverlay, -1, 0, 0, 0, 1);
        drawVertex(builder, matrix, normal, sprite, xMin, yMin, zMax, r, g, b, a, packedLight, packedOverlay, -1, 0, 0, 1, 1);
        drawVertex(builder, matrix, normal, sprite, xMin, yMax, zMax, r, g, b, a, packedLight, packedOverlay, -1, 0, 0, 1, 0);

        drawVertex(builder, matrix, normal, sprite, xMax, yMax, zMax, r, g, b, a, packedLight, packedOverlay, 1, 0, 0, 0, 0);
        drawVertex(builder, matrix, normal, sprite, xMax, yMin, zMax, r, g, b, a, packedLight, packedOverlay, 1, 0, 0, 0, 1);
        drawVertex(builder, matrix, normal, sprite, xMax, yMin, zMin, r, g, b, a, packedLight, packedOverlay, 1, 0, 0, 1, 1);
        drawVertex(builder, matrix, normal, sprite, xMax, yMax, zMin, r, g, b, a, packedLight, packedOverlay, 1, 0, 0, 1, 0);

        poseStack.popPose();
    }

    private void drawVertex(VertexConsumer builder, Matrix4f matrix, Matrix3f normal, TextureAtlasSprite sprite,
                            float x, float y, float z, float r, float g, float b, float a,
                            int light, int overlay, float nx, float ny, float nz, float uOffset, float vOffset) {
        float u = (uOffset > 0.5f) ? sprite.getU1() : sprite.getU0();
        float v = (vOffset > 0.5f) ? sprite.getV1() : sprite.getV0();
        builder.vertex(matrix, x, y, z).color(r, g, b, a).uv(u, v).overlayCoords(overlay).uv2(light).normal(normal, nx, ny, nz).endVertex();
    }
}