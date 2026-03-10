package net.agusdropout.bloodyhell.block.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.agusdropout.bloodyhell.block.entity.custom.altar.MainBloodAltarBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class MainBloodAltarRenderer implements BlockEntityRenderer<MainBloodAltarBlockEntity> {

    public MainBloodAltarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MainBloodAltarBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

    }
}