package net.agusdropout.bloodyhell.block.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.agusdropout.bloodyhell.block.custom.altar.MainBloodAltarBlock;
import net.agusdropout.bloodyhell.block.entity.custom.altar.MainBloodAltarBlockEntity;
import net.agusdropout.bloodyhell.particle.ParticleOptions.NoiseSphereParticleOptions;
import net.agusdropout.bloodyhell.util.visuals.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public class MainBloodAltarRenderer implements BlockEntityRenderer<MainBloodAltarBlockEntity> {

    private static final Vector3f HEART_COLOR = new Vector3f(0.8f, 0.05f, 0.05f);

    public MainBloodAltarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MainBloodAltarBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        long gameTime = be.getLevel().getGameTime();
        renderItem( be, be.getLevel().getGameTime() + partialTick, poseStack, bufferSource, packedLight, packedOverlay);


        if (!be.getBlockState().getValue(MainBloodAltarBlock.ACTIVE)) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5, 1.8, 0.5);


        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lightning());
        RenderHelper.renderAtlasHeart(consumer, poseStack.last().pose(), null,
                gameTime, partialTick,
                HEART_COLOR.x(), HEART_COLOR.y(), HEART_COLOR.z(),
                0.9f, 15728880);

        poseStack.popPose();


        if (gameTime % 40 == 0 && partialTick <= 1.0f && be.getLevel().isClientSide) {
            spawnHeartbeatParticle(be);
        }
    }

    private void spawnHeartbeatParticle(MainBloodAltarBlockEntity be) {
        NoiseSphereParticleOptions options = new NoiseSphereParticleOptions(HEART_COLOR, 1.2f, 30);


        double x = be.getBlockPos().getX() + 0.5;
        double y = be.getBlockPos().getY() + 1.8;
        double z = be.getBlockPos().getZ() + 0.5;

        be.getLevel().addParticle(options, x, y, z, 0, 0, 0);
    }

    private void renderItem( MainBloodAltarBlockEntity be, float time, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (be.hasResultItem()) {
            ItemStack stack = be.getResultItem();
            poseStack.pushPose();

            poseStack.translate(0.5, 1.8, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees((time * 2) % 360));

            float levitationOffset = Mth.sin(time * 0.1f) * 0.05f;
            poseStack.translate(0, levitationOffset, 0);
            poseStack.scale(-0.60f, -0.60f, -0.60f);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack,
                    ItemDisplayContext.FIXED,
                    packedLight,
                    packedOverlay,
                    poseStack,
                    bufferSource,
                    be.getLevel(),
                    0
            );

            poseStack.popPose();
        }
    }
}