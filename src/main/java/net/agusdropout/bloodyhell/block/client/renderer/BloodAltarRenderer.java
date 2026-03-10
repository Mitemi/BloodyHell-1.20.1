package net.agusdropout.bloodyhell.block.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.block.entity.custom.altar.BloodAltarBlockEntity;
import net.agusdropout.bloodyhell.util.ClientTickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BloodAltarRenderer implements BlockEntityRenderer<BloodAltarBlockEntity> {

    public BloodAltarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BloodAltarBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack stack = blockEntity.getItemHandler().getStackInSlot(0);

        if (!stack.isEmpty()) {
            poseStack.pushPose();

            poseStack.translate(0.5D, 1.25D, 0.5D);

            double time = ClientTickHandler.ticksInGame + partialTick;

            poseStack.mulPose(Axis.YP.rotationDegrees((float) (time % 360)));

            float levitationOffset = (float) Math.sin((time % 360) / 10F) * 0.05F;
            poseStack.translate(0F, levitationOffset, 0F);

            poseStack.scale(0.5F, 0.5F, 0.5F);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack,
                    ItemDisplayContext.FIXED,
                    packedLight,
                    packedOverlay,
                    poseStack,
                    bufferSource,
                    blockEntity.getLevel(),
                    0
            );

            poseStack.popPose();
        }
    }
}